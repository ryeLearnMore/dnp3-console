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
package com.automatak.aegis.dnp3.app

import com.automatak.aegis.sapi.SerializableToBytes

object IIN {

  def field(bytes: Byte*): Byte = bytes.foldLeft(0)((sum, byte) => sum | byte).toByte

  def empty: IIN = IIN(0x00.toByte, 0x00.toByte)

  val IIN10_ALL_STATIONS = 0x01.toByte
  val IIN11_CLASS1_EVENTS = 0x02.toByte
  val IIN12_CLASS2_EVENTS = 0x04.toByte
  val IIN13_CLASS3_EVENTS = 0x08.toByte
  val IIN14_NEED_TIME = 0x10.toByte
  val IIN15_LOCAL_CONTROL = 0x20.toByte
  val IIN16_DEVICE_TROUBLE = 0x40.toByte
  val IIN17_DEVICE_RESTART = 0x80.toByte

  val IIN20_FUNC_NOT_SUPPORTED = 0x01.toByte
  val IIN21_OBJECT_UNKNOWN = 0x02.toByte
  val IIN22_PARAM_ERROR = 0x04.toByte
  val IIN23_EVENT_BUFFER_OVERFLOW = 0x08.toByte
  val IIN24_ALREADY_EXECUTING = 0x10.toByte
  val IIN25_CONFIG_CORRUPT = 0x20.toByte
  val IIN26_RESERVED1 = 0x40.toByte
  val IIN27_RESERVED2 = 0x80.toByte

  case class Mask(mask: Byte, name: String)

  val lsbMasks = List(
    Mask(IIN10_ALL_STATIONS, "AllStations"),
    Mask(IIN11_CLASS1_EVENTS, "Class1Events"),
    Mask(IIN12_CLASS2_EVENTS, "Class2Events"),
    Mask(IIN13_CLASS3_EVENTS, "Class3Events"),
    Mask(IIN14_NEED_TIME, "NeedTime"),
    Mask(IIN15_LOCAL_CONTROL, "LocalControl"),
    Mask(IIN16_DEVICE_TROUBLE, "DeviceTrouble"),
    Mask(IIN17_DEVICE_RESTART, "DeviceRestart"))

  val msbMasks = List(
    Mask(IIN20_FUNC_NOT_SUPPORTED, "FuncNotSupported"),
    Mask(IIN21_OBJECT_UNKNOWN, "ObjectUnknown"),
    Mask(IIN22_PARAM_ERROR, "ParamError"),
    Mask(IIN23_EVENT_BUFFER_OVERFLOW, "EventBufferOverflow"),
    Mask(IIN24_ALREADY_EXECUTING, "AlreadyExecuting"),
    Mask(IIN25_CONFIG_CORRUPT, "ConfigCorrupt"),
    Mask(IIN26_RESERVED1, "Reserved1"),
    Mask(IIN27_RESERVED2, "Reserved2"))

  def byteToString(byte: Byte, lookup: List[Mask]) = {
    val options = lookup.map(mask =>
      if ((byte & mask.mask) != 0) Some(mask.name)
      else None).flatten.mkString(", ")
    "[" + options + "]"
  }

  import com.automatak.aegis.sapi.toHex

  def byteToHex(byte: Byte) = "0x" + toHex(byte) + ": "
}

case class IIN(lsb: Byte, msb: Byte) extends SerializableToBytes {

  def isEmpty: Boolean = (lsb == 0.toByte) && (msb == 0.toByte)
  def nonEmpty: Boolean = !isEmpty

  def unary_~ = IIN((~lsb).toByte, (~msb).toByte)
  def -(rhs: IIN): IIN = this & (~rhs)
  def |(rhs: IIN): IIN = IIN((lsb | rhs.lsb).toByte, (msb | rhs.msb).toByte)
  def &(rhs: IIN): IIN = IIN((lsb & rhs.lsb).toByte, (msb & rhs.msb).toByte)

  import IIN._

  override def toBytes = List(lsb, msb)
  override def toString = "IIN(" + byteToHex(lsb) + byteToString(lsb, lsbMasks) + " : " + byteToHex(msb) + byteToString(msb, msbMasks) + ")"

}
