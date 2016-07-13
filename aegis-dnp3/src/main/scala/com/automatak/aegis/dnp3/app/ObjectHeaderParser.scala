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

import com.automatak.aegis.sapi.{ UInt16LE, UInt32LE, UInt8 }

trait ObjectHeaderParser {
  def qualifier: Byte
  def parse(group: Byte, variation: Byte, bytes: List[Byte]): (ObjectHeader, List[Byte])
}

object ObjectHeaderParser {

  private val parsers = List(
    AllObjectsParser,
    OneByteStartStopParser,
    TwoByteStartStopParser,
    FourByteStartStopParser,
    OneByteCountParser,
    TwoByteCountParser,
    FourByteCountParser,
    OneByteCountOneByteIndexParser,
    TwoByteCountTwoByteIndexParser,
    FourByteCountFourByteIndexParser)

  private val map = parsers.foldLeft(Map.empty[Byte, ObjectHeaderParser]) { (map, p) =>
    if (map.get(p.qualifier).isDefined) throw new Exception("Parser already defined")
    else map + (p.qualifier -> p)
  }

  def apply(qualifier: Byte): Option[ObjectHeaderParser] = map.get(qualifier)

  def splitData(bytes: List[Byte], size: Int): (List[Byte], List[Byte]) = {
    val (left, right) = bytes.splitAt(size)
    if (left.size != size) {
      throw new Exception("Not enough bytes for object data")
    } else (left, right)
  }
}

object AllObjectsParser extends ObjectHeaderParser {

  def qualifier: Byte = ObjectHeader.QC_ALL_OBJ

  def parse(group: Byte, variation: Byte, bytes: List[Byte]): (ObjectHeader, List[Byte]) =
    (AllObjectsHeader(GroupVariation(group, variation)), bytes)
}

object OneByteStartStopParser extends ObjectHeaderParser {

  def qualifier: Byte = ObjectHeader.QC_1B_START_STOP

  def parse(group: Byte, variation: Byte, bytes: List[Byte]): (ObjectHeader, List[Byte]) = bytes match {
    case start :: stop :: remainder =>
      val count = stop - start + 1
      if (count < 0) throw new Exception("Invalid start/stop: " + start + "/" + stop)
      else {
        val gv = GroupVariation(group, variation)
        gv.size(count) match {
          case None => throw new Exception("Start/stop header may not be used with group/variation: " + gv)
          case Some(size) =>
            val (data, theRest) = ObjectHeaderParser.splitData(remainder, size)
            (OneByteStartStopHeader(gv, UInt8(start), UInt8(stop), data), theRest)
        }
      }
    case _ => throw new Exception("Not enough data for object header")
  }
}

object TwoByteStartStopParser extends ObjectHeaderParser {

  def qualifier: Byte = ObjectHeader.QC_2B_START_STOP

  def parse(group: Byte, variation: Byte, bytes: List[Byte]): (ObjectHeader, List[Byte]) = bytes match {
    case start1 :: start2 :: stop1 :: stop2 :: remainder =>
      val start = UInt16LE(start1, start2)
      val stop = UInt16LE(stop1, stop2)
      val count = stop - start + 1
      if (count < 0) throw new Exception("Invalid start/stop: " + start + "/" + stop)
      else {
        val gv = GroupVariation(group, variation)
        gv.size(count) match {
          case None => throw new Exception("Start/stop header may not be used with group/variation: " + gv)
          case Some(size) =>
            val (data, theRest) = ObjectHeaderParser.splitData(remainder, size)
            (TwoByteStartStopHeader(gv, start, stop, data), theRest)
        }
      }
    case _ => throw new Exception("Not enough data for object header")
  }
}

object FourByteStartStopParser extends ObjectHeaderParser {

  def qualifier: Byte = ObjectHeader.QC_4B_START_STOP

  def parse(group: Byte, variation: Byte, bytes: List[Byte]): (ObjectHeader, List[Byte]) = bytes match {
    case start1 :: start2 :: start3 :: start4 :: stop1 :: stop2 :: stop3 :: stop4 :: remainder =>
      val start = UInt32LE(start1, start2, start3, start4)
      val stop = UInt32LE(stop1, stop2, stop3, stop4)
      val count = stop - start + 1
      if (count < 0) throw new Exception("Invalid start/stop: " + start + "/" + stop)
      else {
        val gv = GroupVariation(group, variation)
        gv.size(count.toInt) match {
          case None => throw new Exception("Start/stop header may not be used with group/variation: " + gv)
          case Some(size) =>
            val (data, theRest) = ObjectHeaderParser.splitData(remainder, size)
            (FourByteStartStopHeader(gv, start, stop, data), theRest)
        }
      }
    case _ => throw new Exception("Not enough data for object header")
  }
}

object OneByteCountParser extends ObjectHeaderParser {

  def qualifier: Byte = ObjectHeader.QC_1B_CNT

  def parse(group: Byte, variation: Byte, bytes: List[Byte]): (ObjectHeader, List[Byte]) = bytes match {
    case cnt :: remainder =>
      val count = UInt8(cnt)
      val gv = GroupVariation(group, variation)
      gv.size(count.value) match {
        case None => throw new Exception("count header may not be used with group/variation: " + gv)
        case Some(size) =>
          val (data, theRest) = ObjectHeaderParser.splitData(remainder, size)
          (OneByteCountHeader(gv, count, data), theRest)
      }
    case _ => throw new Exception("Not enough data for object header")
  }
}

object TwoByteCountParser extends ObjectHeaderParser {

  def qualifier: Byte = ObjectHeader.QC_2B_CNT

  def parse(group: Byte, variation: Byte, bytes: List[Byte]): (ObjectHeader, List[Byte]) = bytes match {
    case cnt1 :: cnt2 :: remainder =>
      val count = UInt16LE(cnt1, cnt2)
      val gv = GroupVariation(group, variation)
      gv.size(count) match {
        case None => throw new Exception("count header may not be used with this group/variation")
        case Some(size) =>
          val (data, theRest) = ObjectHeaderParser.splitData(remainder, size)
          (TwoByteCountHeader(gv, count, data), theRest)
      }
    case _ => throw new Exception("Not enough data for object header")
  }
}

object FourByteCountParser extends ObjectHeaderParser {

  def qualifier: Byte = ObjectHeader.QC_4B_CNT

  def parse(group: Byte, variation: Byte, bytes: List[Byte]): (ObjectHeader, List[Byte]) = bytes match {
    case cnt1 :: cnt2 :: cnt3 :: cnt4 :: remainder =>
      val count = UInt32LE(cnt1, cnt2, cnt4, cnt4)
      val gv = GroupVariation(group, variation)
      gv.size(count.i.toInt) match {
        case None => throw new Exception("count header may not be used with this group/variation")
        case Some(size) =>
          val (data, theRest) = ObjectHeaderParser.splitData(remainder, size)
          (FourByteCountHeader(gv, count, data), theRest)
      }
    case _ => throw new Exception("Not enough data for object header")
  }
}

object OneByteCountOneByteIndexParser extends ObjectHeaderParser {

  def qualifier: Byte = ObjectHeader.QC_1B_CNT_1B_INDEX

  def parse(group: Byte, variation: Byte, bytes: List[Byte]): (ObjectHeader, List[Byte]) = bytes match {
    case cnt :: remainder =>
      val count = UInt8(cnt)
      val gv = GroupVariation(group, variation)
      gv.size(count.value) match {
        case None => throw new Exception("count header may not be used with group/variation: " + gv)
        case Some(size) =>
          val sizeWithIndices = count.value + size
          val (data, theRest) = ObjectHeaderParser.splitData(remainder, sizeWithIndices)
          (OneByteCountOneByteIndexHeader(gv, count, data), theRest)
      }
    case _ => throw new Exception("Not enough data for object header")
  }
}

object TwoByteCountTwoByteIndexParser extends ObjectHeaderParser {

  def qualifier: Byte = ObjectHeader.QC_2B_CNT_2B_INDEX

  def parse(group: Byte, variation: Byte, bytes: List[Byte]): (ObjectHeader, List[Byte]) = bytes match {
    case cnt1 :: cnt2 :: remainder =>
      val count = UInt16LE(cnt1, cnt2)
      val gv = GroupVariation(group, variation)
      gv.size(count.value) match {
        case None => throw new Exception("count header may not be used with group/variation: " + gv)
        case Some(size) =>
          val sizeWithIndices = 2 * count.value + size
          val (data, theRest) = ObjectHeaderParser.splitData(remainder, sizeWithIndices)
          (TwoByteCountTwoByteIndexHeader(gv, count, data), theRest)
      }
    case _ => throw new Exception("Not enough data for object header")
  }
}

object FourByteCountFourByteIndexParser extends ObjectHeaderParser {

  def qualifier: Byte = ObjectHeader.QC_4B_CNT_4B_INDEX

  def parse(group: Byte, variation: Byte, bytes: List[Byte]): (ObjectHeader, List[Byte]) = bytes match {
    case cnt1 :: cnt2 :: cnt3 :: cnt4 :: remainder =>
      val count = UInt32LE(cnt1, cnt2, cnt4, cnt4)
      val gv = GroupVariation(group, variation)
      gv.size(count.i.toInt) match {
        case None => throw new Exception("count header may not be used with group/variation: " + gv)
        case Some(size) =>
          val sizeWithIndices = 4 * count.value + size
          val (data, theRest) = ObjectHeaderParser.splitData(remainder, sizeWithIndices.toInt)
          (FourByteCountFourByteIndexHeader(gv, count, data), theRest)
      }
    case _ => throw new Exception("Not enough data for object header")
  }
}