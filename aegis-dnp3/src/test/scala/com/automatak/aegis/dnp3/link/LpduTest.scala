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
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

@RunWith(classOf[JUnitRunner])
class LpduTest extends FunSuite with Matchers {

  test("Control byte generation") {
    val ctrl = LinkCtrl(true, true, true, true, Lpdu.CONFIRMED_USER_DATA)
    ctrl.toByte should equal(0xF3.toByte)
  }

  def testGeneration(expected: String)(gen: => List[Byte]) = toHex(gen) should equal(expected)

  test("Lpdu w/o data") {
    testGeneration("05 64 05 C0 01 00 00 04 E9 21") {
      val ctrl = LinkCtrl(true, true, false, false, Lpdu.RESET_LINK_STATES)
      LinkHeader(UInt8(5), ctrl, UInt16LE(1), UInt16LE(1024)).toBytes
    }
  }

  test("Lpdu w/ data") {
    testGeneration("05 64 14 F3 01 00 00 04 0A 3B C0 C3 01 3C 02 06 3C 03 06 3C 04 06 3C 01 06 9A 12") {
      val ctrl = LinkCtrl(true, true, true, true, Lpdu.CONFIRMED_USER_DATA)
      val data = fromHex("C0 C3 01 3C 02 06 3C 03 06 3C 04 06 3C 01 06")
      val hdr = LinkHeader(UInt8((5 + data.length).toShort), ctrl, UInt16LE(1), UInt16LE(1024))
      Lpdu(hdr, data.toList).toBytes
    }
  }

  def testBodySize(len: Int, expected: Int) =
    Crc.interlace(FixedBytes.repeat(0xFF.toByte)(len)).length should equal(expected)

  test("Body size 0")(testBodySize(0, 0))
  test("Body size 1")(testBodySize(1, 3))
  test("Body size 16")(testBodySize(16, 18))
  test("Body size 17")(testBodySize(17, 21))
  test("Body size 250")(testBodySize(250, 282))

}