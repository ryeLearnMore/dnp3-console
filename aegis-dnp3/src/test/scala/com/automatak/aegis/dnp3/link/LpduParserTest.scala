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

/**
 * Copyright 2011 John Adam Crain (jadamcrain@gmail.com)
 *
 * This file is the sole property of the copyright owner and is NOT
 * licensed to any 3rd parties.
 */
import java.nio.ByteBuffer

import com.automatak.aegis.sapi._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

@RunWith(classOf[JUnitRunner])
class LpduParserTest extends FunSuite with Matchers {

  test("Parses example frame 1") {
    val buff = ByteBuffer.wrap(fromHex("05 64 05 C0 01 00 00 04 E9 21").toArray)
    val ctrl = LinkCtrl(true, true, false, false, Lpdu.RESET_LINK_STATES)
    val header = LinkHeader(UInt8(5), ctrl, UInt16LE(1), UInt16LE(1024))
    LpduParser.decode(buff) should equal(Some(Lpdu(header)))
    buff.position() should equal(0)
    buff.remaining() should equal(10)
  }

  test("Parses user data") {
    val buff = ByteBuffer.wrap(fromHex("05 64 14 F3 01 00 00 04 0A 3B C0 C3 01 3C 02 06 3C 03 06 3C 04 06 3C 01 06 9A 12").toArray)
    val userData = fromHex("C0 C3 01 3C 02 06 3C 03 06 3C 04 06 3C 01 06").toList
    val lpdu = LpduParser.decode(buff)
    val ctrl = LinkCtrl(true, true, true, true, Lpdu.CONFIRMED_USER_DATA)
    val header = LinkHeader(UInt8(20), ctrl, UInt16LE(1), UInt16LE(1024))
    lpdu should equal(Some(Lpdu(header, userData)))
  }

  test("Catches CRC failures in the body") {
    val buff = ByteBuffer.wrap(fromHex("05 64 14 F3 01 00 00 04 0A 3B C0 C3 01 3C 02 06 3C 03 06 3C 04 06 3C 01 06 9A 1F").toArray)
    LpduParser.decode(buff).isEmpty should equal(true)
  }

  test("Parses partially arrived data frames") {

    // linkConfirmed user data in two chunks
    val block1 = fromHex("05 64 14 F3 01 00 00 04 0A 3B C0")
    val block2 = fromHex("C3 01 3C 02 06 3C 03 06 3C 04 06 3C 01 06 9A 12")

    val userData = fromHex("C0 C3 01 3C 02 06 3C 03 06 3C 04 06 3C 01 06").toList

    val buffer = ByteBuffer.allocate(292)

    buffer.put(block1.toArray)
    buffer.flip()

    LpduParser.decode(buffer).isEmpty should equal(true)

    buffer.put(block2.toArray)
    buffer.flip()

    val lpdu = LpduParser.decode(buffer)
    val ctrl = LinkCtrl(true, true, true, true, Lpdu.CONFIRMED_USER_DATA)
    val header = LinkHeader(UInt8(20), ctrl, UInt16LE(1), UInt16LE(1024))
    lpdu should equal(Some(Lpdu(header, userData)))
  }

  test("Buffers frame until they're requested") {
    val buff = ByteBuffer.wrap(fromHex("05 64 05 C0 01 00 00 04 E9 21 05 64 05 C0 01 00 00 04 E9 21").toArray) //two valid frames back to back
    LpduParser.decode(buff).isEmpty should equal(false)
    buff.flip()
    LpduParser.decode(buff).isEmpty should equal(false)
    buff.flip()
    LpduParser.decode(buff).isEmpty should equal(true)
  }

  test("Buffers data frame after link frame") {
    val example = "05 64 05 C0 01 00 00 04 E9 21"
    val userData = "05 64 14 F3 01 00 00 04 0A 3B C0 C3 01 3C 02 06 3C 03 06 3C 04 06 3C 01 06 9A 12"
    val buff = ByteBuffer.wrap((fromHex(example) ::: fromHex(userData)).toArray)
    assert(LpduParser.decode(buff).isDefined)
    buff.flip()
    assert(LpduParser.decode(buff).isDefined)
    buff.flip()
    assert(LpduParser.decode(buff).isEmpty)

  }

  test("Crc failure yields no frame") {
    val buff = ByteBuffer.wrap(fromHex("05 64 05 C0 01 00 00 04 E9 20").toArray)
    LpduParser.decode(buff) should equal(None)
    buff.position() should equal(8)
    buff.remaining() should equal(2)
  }

  test("Less than 10 bytes has no effect") {
    val buff = ByteBuffer.wrap(fromHex("05 64 05 C0").toArray)
    LpduParser.decode(buff) should equal(None)
    buff.remaining() should equal(0)
  }

}