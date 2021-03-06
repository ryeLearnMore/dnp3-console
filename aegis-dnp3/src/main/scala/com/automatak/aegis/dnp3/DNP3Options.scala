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
package com.automatak.aegis.dnp3

import com.automatak.aegis.sapi._

case class DNP3Options(dest: UInt16LE, src: UInt16LE, skipPreTest: Boolean, skipPostTest: Boolean, function: Byte, appseq: Byte, headers: List[Byte], retries: Int, linkTimeout: Int, appTimeout: Int)

object DNP3Options {

  def readConfig(dictionary: Dictionary): Result[DNP3Options] = {
    for {
      dest <- dest.get(dictionary)
      src <- src.get(dictionary)
      func <- function.get(dictionary)
      seq <- appSeq.get(dictionary)
      retries <- retries.get(dictionary)
      ltimeout <- linkTimeout.get(dictionary)
      atimeout <- appTimeout.get(dictionary)
      data <- headers.get(dictionary)
    } yield DNP3Options(
      UInt16LE(dest),
      UInt16LE(src),
      skipPreTest.get(dictionary),
      skipPostTest.get(dictionary),
      func.toByte,
      seq.toByte,
      fromHex(data),
      retries,
      ltimeout,
      atimeout)
  }

  private val dest = IntegerOption("dest", "link layer destination", IntMinMax.bounds(0, 65535), Some(1024))
  private val src = IntegerOption("src", "link layer source", IntMinMax.bounds(0, 65535), Some(1))
  private val skipPreTest = BooleanFlag("skippretest", "Do not query link status before sending ASDU")
  private val skipPostTest = BooleanFlag("skipposttest", "Do not query link status after sending ASDU")
  private val function = IntegerOption("func", "app layer function", IntMinMax.bounds(0, 255), Some(1))
  private val appSeq = IntegerOption("appseq", "app layer sequence #", IntMinMax.bounds(0, 15), Some(0))
  private val headers = StringOption("headers", "application layer data to transmit in hex", Some(""))
  private val retries = IntegerOption("retries", "Number of link status retries", IntMinMax.bounds(3, 1000), Some(2))
  private val linkTimeout = IntegerOption("linktimeout", "link layer timeout in milliseconds", IntMinMax.minOnly(10), Some(1000))
  private val appTimeout = IntegerOption("apptimeout", "application layer timeout in milliseconds", IntMinMax.minOnly(30), Some(2000))

  def asList: List[ProcedureOption] = List(
    dest, src, skipPreTest, skipPostTest, function, appSeq, headers, retries, linkTimeout, appTimeout)

}
