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

import com.automatak.aegis.sapi._

object ObjectHeader {

  val QC_1B_START_STOP = 0x00.toByte
  val QC_2B_START_STOP = 0x01.toByte
  val QC_4B_START_STOP = 0x02.toByte

  val QC_ALL_OBJ = 0x06.toByte

  val QC_1B_CNT = 0x07.toByte
  val QC_2B_CNT = 0x08.toByte
  val QC_4B_CNT = 0x09.toByte

  val QC_1B_CNT_1B_INDEX = 0x17.toByte
  val QC_2B_CNT_2B_INDEX = 0x28.toByte
  val QC_4B_CNT_4B_INDEX = 0x39.toByte

  val QC_1B_VCNT_1B_SIZE = 0x4B.toByte
  val QC_1B_VCNT_2B_SIZE = 0x5B.toByte
  val QC_1B_VCNT_4B_SIZE = 0x6B.toByte

}

sealed trait ObjectHeader extends SerializableToBytes {
  def groupVariation: GroupVariation
  def qualifier: Byte
  protected def qualString: String
  protected def dataString: String
  protected def dataBytes: List[Byte]
  final override def toString = qualString + "(" + toHexString(qualifier) + ") " + groupVariation.toString + " " + dataString
  override def toBytes: List[Byte] = groupVariation.toBytes ::: List(qualifier) ::: dataBytes
}

final case class MockObjectHeader(data: List[Byte]) extends ObjectHeader {

  // no of there matter
  def groupVariation = MockGroupVaration
  override def qualifier: Byte = ObjectHeader.QC_ALL_OBJ
  override def qualString = "Mock Header"
  override def dataString = ""
  override def dataBytes = Nil

  override def toBytes = data
}

final case class AllObjectsHeader(groupVariation: GroupVariation) extends ObjectHeader {
  override def qualifier: Byte = ObjectHeader.QC_ALL_OBJ
  override def qualString = "All Objects"
  override def dataString = ""
  override def dataBytes = Nil
}

final case class OneByteStartStopHeader(groupVariation: GroupVariation, start: UInt8, stop: UInt8, data: List[Byte]) extends ObjectHeader {
  override def qualifier: Byte = ObjectHeader.QC_1B_START_STOP
  override def qualString = "1 Byte Start/Stop"
  override def dataString = "start: " + start.value + " stop: " + stop.value + " data: [" + toHex(data) + "]"
  override def dataBytes = start.toBytes ::: stop.toBytes ::: data
}

final case class TwoByteStartStopHeader(groupVariation: GroupVariation, start: UInt16LE, stop: UInt16LE, data: List[Byte]) extends ObjectHeader {
  override def qualifier: Byte = ObjectHeader.QC_2B_START_STOP
  override def qualString = "2 Byte Start/Stop"
  override def dataString = "start: " + start.value + " stop: " + stop.value + " data: [" + toHex(data) + "]"
  override def dataBytes = start.toBytes ::: stop.toBytes ::: data
}

final case class FourByteStartStopHeader(groupVariation: GroupVariation, start: UInt32LE, stop: UInt32LE, data: List[Byte]) extends ObjectHeader {
  override def qualifier: Byte = ObjectHeader.QC_4B_START_STOP
  override def qualString = "4 Byte Start/Stop"
  override def dataString = "start: " + start.value + " stop: " + stop.value + " data: [" + toHex(data) + "]"
  override def dataBytes = start.toBytes ::: stop.toBytes ::: data
}

final case class OneByteCountHeader(groupVariation: GroupVariation, count: UInt8, data: List[Byte]) extends ObjectHeader {
  override def qualifier: Byte = ObjectHeader.QC_1B_CNT
  override def qualString = "1 Byte Count"
  override def dataString = "count: " + count.value + " data: [" + toHex(data) + "]"
  override def dataBytes = count.toBytes ::: data
}

final case class TwoByteCountHeader(groupVariation: GroupVariation, count: UInt16LE, data: List[Byte]) extends ObjectHeader {
  override def qualifier: Byte = ObjectHeader.QC_2B_CNT
  override def qualString = "2 Byte Count"
  override def dataString = "count: " + count.value + " data: [" + toHex(data) + "]"
  override def dataBytes = count.toBytes ::: data
}

final case class FourByteCountHeader(groupVariation: GroupVariation, count: UInt32LE, data: List[Byte]) extends ObjectHeader {
  override def qualifier: Byte = ObjectHeader.QC_4B_CNT
  override def qualString = "4 Byte Count"
  override def dataString = "count: " + count.value + " data: [" + toHex(data) + "]"
  override def dataBytes = count.toBytes ::: data
}

final case class OneByteCountOneByteIndexHeader(groupVariation: GroupVariation, count: UInt8, data: List[Byte]) extends ObjectHeader {
  override def qualifier: Byte = ObjectHeader.QC_1B_CNT_1B_INDEX
  override def qualString = "One Byte Count, One Byte Index"
  override def dataString = "count: " + count.value + " data: [" + toHex(data) + "]"
  override def dataBytes = count.toBytes ::: data
}

final case class TwoByteCountTwoByteIndexHeader(groupVariation: GroupVariation, count: UInt16LE, data: List[Byte]) extends ObjectHeader {
  override def qualifier: Byte = ObjectHeader.QC_2B_CNT_2B_INDEX
  override def qualString = "Two Byte Count, Two Byte Index"
  override def dataString = "count: " + count.value + " data: [" + toHex(data) + "]"
  override def dataBytes = count.toBytes ::: data
}

final case class FourByteCountFourByteIndexHeader(groupVariation: GroupVariation, count: UInt32LE, data: List[Byte]) extends ObjectHeader {
  override def qualifier: Byte = ObjectHeader.QC_4B_CNT_4B_INDEX
  override def qualString = "Four Byte Count, Four Byte Index"
  override def dataString = "count: " + count.value + " data: [" + toHex(data) + "]"
  override def dataBytes = count.toBytes ::: data
}