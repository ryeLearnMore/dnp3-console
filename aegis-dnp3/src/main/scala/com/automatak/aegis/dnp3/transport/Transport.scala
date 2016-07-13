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
package com.automatak.aegis.dnp3.transport

object Transport {

  def next(seq: Int) = rotate(seq + 1)
  def rotate(seq: Int) = (seq % 64).toByte

  val finMask = 0x80.toByte
  val firMask = 0x40.toByte
  val seqMask = 0x3F.toByte

  def generate(data: List[Byte], seq: Byte, firCorrect: Boolean, finCorrect: Boolean): List[Byte] = {
    Transport.Header(firCorrect, finCorrect, seq).toByte :: data
  }

  case class Header(fir: Boolean, fin: Boolean, seq: Byte) {
    override def toString(): String = "fir: " + fir + " fin: " + fin + " seq: " + seq.toInt
    def toByte = function(fir, fin, seq)
  }

  def function(byte: Byte): Header = {
    val fir = (byte & firMask) != 0
    val fin = (byte & finMask) != 0
    val seq = (byte & seqMask).toByte
    Header(fir, fin, seq)
  }

  def function(fir: Boolean, fin: Boolean, seq: Byte): Byte = {
    val firData = if (fir) firMask else 0
    val finData = if (fin) finMask else 0
    val seqData = seq & seqMask
    (firData | finData | seqData).toByte
  }

}