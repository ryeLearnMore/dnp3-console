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

import scala.annotation.tailrec

// This is a DNP specific CRC algorithm

trait CrcGenerator {

  def calc(arr: Array[Byte]): Short

  def calc(list: List[Byte]): Short = calc(list.toArray)

  def append(arr: List[Byte]): List[Byte] = assemble(arr, SInt16LE(calc(arr)))

  def interlace(arr: List[Byte]): List[Byte] = arr.grouped(16).foldLeft(List.empty[Byte])((sum, x) => sum ++ append(x))
}

object Crc extends CrcGenerator {

  // pre-compute the lookup table, happens once doesn't need to be efficient
  private val table = {
    (0 to 255).foldLeft[List[Int]](Nil) { (s, i) =>
      (0 to 7).foldLeft(i) { (crc, j) =>
        if ((crc & 0x1) != 0) (crc >> 1) ^ 0xA6BC else crc >> 1
      } :: s
    }.reverse.toArray
  }

  def calc(arr: Array[Byte]): Short = {
    @tailrec
    def calcCrc(crc: Int, pos: Int): Short = {
      if (pos == arr.length) ((~crc) & 0xFFFF).toShort
      else calcCrc(table((crc ^ arr(pos)) & 0xFF) ^ (crc >> 8), pos + 1)
    }
    calcCrc(0, 0)
  }

}
