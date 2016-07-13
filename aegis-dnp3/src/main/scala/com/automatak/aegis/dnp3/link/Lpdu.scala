/**
 *           _    _ _______ ____  __  __       _______       _  __
 *      /\  | |  | |__   __/ __ \|  \/  |   /\|__   __|/\   | |/ /
 *     /  \ | |  | |  | | | |  | | \  / |  /  \  | |  /  \  | ' /
 *    / /\ \| |  | |  | | | |  | | |\/| | / /\ \ | | / /\ \ |  <
 *   / ____ \ |__| |  | | | |__| | |  | |/ ____ \| |/ ____ \| . \
 *  /_/    \_\____/   |_|  \____/|_|  |_/_/    \_\_/_/    \_\_|\_\
 *
 *
 * Licensed to Automatak LLC (www.automatak.com) under one or
 * more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Automatak LLC licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.automatak.aegis.dnp3.link

import com.automatak.aegis.sapi._

object LinkCtrl {
  def apply(byte: Byte): LinkCtrl = {
    val master = (byte & 0x80) != 0
    val pri = (byte & 0x40) != 0
    val fcb = (byte & 0x20) != 0
    val fcvdfc = (byte & 0x10) != 0
    val func: Byte = (byte & 0x0F).toByte

    LinkCtrl(master, pri, fcb, fcvdfc, func)
  }
}

case class LinkCtrl(master: Boolean, pri: Boolean, fcb: Boolean, fcvdfc: Boolean, func: Byte) extends SerializableToBytes {

  assert(func <= 0xF)

  def toByte: Byte = (Bitfield.fromTop(master, pri, fcb, fcvdfc) | (func & 0xF)).toByte

  override def toBytes = List(toByte)

  override def toString: String = "master: " + master + " pri: " + pri + " fcb: " + fcb + " fcv: " + fcvdfc + " func: " + Lpdu.convertFuncToString(pri, func) + "(0x" + toHex(func) + ")" + " 0x" + toHex(toByte)
}

case class LinkHeader(len: UInt8, ctrl: LinkCtrl, dest: UInt16LE, src: UInt16LE) extends SerializableToBytes {

  private def bytes = Lpdu.sync ++ bytesNoSync

  private def bytesNoSync = len ++ ctrl ++ dest ++ src

  private def toBytesWithGen(gen: CrcGenerator) = gen.append(bytes)

  def crc = Crc.calc(bytes)

  override def toBytes = toBytesWithGen(Crc)

  override def toString: String = ctrl.toString + " length: " + len.value + " dest: " + dest.value + " src: " + src.value
}

case class Lpdu(header: LinkHeader, data: List[Byte]) extends SerializableToBytes {

  override def toBytes = header.toBytes ++ Crc.interlace(data)

  override def toString = header.toString
}

object Lpdu {

  def withData(ctrl: LinkCtrl, dest: UInt16LE, src: UInt16LE, data: List[Byte]): Lpdu = {
    val len = UInt8((data.size + 5).toShort)
    Lpdu(LinkHeader(len, ctrl, dest, src), data)
  }

  def apply(header: LinkHeader): Lpdu = Lpdu(header, List.empty[Byte])

  def fixedPayload(header: LinkHeader, random: RandomGenerator): Lpdu = {
    val num = math.max(header.len - 5, 0)
    Lpdu(header, random.bytes(num))
  }

  def formatRequestLinkStatus(master: Boolean, src: Int, dest: Int) =
    LinkHeader(UInt8(5), LinkCtrl(master, true, false, false, Lpdu.REQUEST_LINK_STATES), UInt16LE(src), UInt16LE(dest))

  def unconfirmedUserData(master: Boolean, dest: Int, src: Int, data: List[Byte]): Lpdu =
    Lpdu(LinkHeader(UInt8((5 + data.size).toShort), LinkCtrl(master, true, false, false, Lpdu.UNCONFIRMED_USER_DATA), UInt16LE(dest), UInt16LE(src)), data)

  val sync = List(0x05.toByte, 0x64.toByte)

  def convertFuncToString(pri: Boolean, func: Byte): String = {
    if (pri) func match {
      case RESET_LINK_STATES => "RESET_LINK_STATES"
      case TEST_LINK_STATES => "TEST_LINK_STATES"
      case CONFIRMED_USER_DATA => "CONFIRMED_USER_DATA"
      case UNCONFIRMED_USER_DATA => "UNCONFIRMED_USER_DATA"
      case REQUEST_LINK_STATES => "REQUEST_LINK_STATES"
      case _ => "UNKNOWN"
    }
    else func match {
      case ACK => "ACK"
      case NACK => "NACK"
      case LINK_STATUS => "LINK_STATUS"
      case NOT_SUPPORTED => "NOT_SUPPORTED"
      case _ => "UNKNOWN"
    }
  }

  val RESET_LINK_STATES = 0.toByte
  val TEST_LINK_STATES = 2.toByte
  val CONFIRMED_USER_DATA = 3.toByte
  val UNCONFIRMED_USER_DATA = 4.toByte
  val REQUEST_LINK_STATES = 9.toByte

  val ACK = 0.toByte
  val NACK = 1.toByte
  val LINK_STATUS = 0x0B.toByte
  val NOT_SUPPORTED = 0x0F.toByte

  val funcs = List(
    RESET_LINK_STATES,
    TEST_LINK_STATES,
    CONFIRMED_USER_DATA,
    UNCONFIRMED_USER_DATA,
    REQUEST_LINK_STATES,
    ACK,
    NACK,
    LINK_STATUS,
    NOT_SUPPORTED)
}
