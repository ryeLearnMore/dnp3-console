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

import com.automatak.aegis.dnp3.app.objects._
import com.automatak.aegis.sapi._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

@RunWith(classOf[JUnitRunner])
class ApduFormatingTest extends FunSuite with Matchers {

  test("Integrity poll generation") {
    val integrity = "C0 01 3C 01 06" // read class 0
    val ctrl = AppCtrl(true, true, false, false, 0)
    val apdu = Apdu(ctrl, AppFunctions.read, None, List(AllObjectsHeader(Group60Var1)))
    toHex(apdu.toBytes) should equal(integrity)
    val parsed = Apdu(fromHex(integrity))
    parsed should equal(apdu)
  }

  test("Read all events") {
    val eventScan = "C0 01 3C 02 06 3C 03 06 3C 04 06" // read class 1,2,3
    val ctrl = AppCtrl(true, true, false, false, 0)
    val headers = List(AllObjectsHeader(Group60Var2), AllObjectsHeader(Group60Var3), AllObjectsHeader(Group60Var4))
    val apdu = Apdu(ctrl, AppFunctions.read, None, headers)
    toHex(apdu.toBytes) should equal(eventScan)
    val parsed = Apdu(fromHex(eventScan))
    parsed should equal(apdu)
  }

  test("Read simple response w/ IIN") {
    val rsp = "C0 81 00 00"
    val ctrl = AppCtrl(true, true, false, false, 0)
    val apdu = Apdu(ctrl, AppFunctions.rsp, Some(IIN(0, 0)), Nil)
    toHex(apdu.toBytes) should equal(rsp)
    val parsed = Apdu(fromHex(rsp))
    parsed should equal(apdu)
  }

  test("Read simple response with Group1Var2") {
    val rsp = "C0 81 00 00 01 02 00 00 00 01"
    val ctrl = AppCtrl(true, true, false, false, 0)
    val headers = OneByteStartStopHeader(Group1Var2, UInt8(0), UInt8(0), Group1Var2(0x01).toBytes)
    val apdu = Apdu(ctrl, AppFunctions.rsp, Some(IIN(0, 0)), List(headers))
    toHex(apdu.toBytes) should equal(rsp)
    val parsed = Apdu(fromHex(rsp))
    parsed should equal(apdu)
  }

  test("Read four byte count header w/ zero elements") {
    val rsp = "C0 81 00 00 01 02 09 00 00 00 00"
    val ctrl = AppCtrl(true, true, false, false, 0)
    val headers = FourByteCountHeader(Group1Var2, UInt32LE(0), Nil)
    val apdu = Apdu(ctrl, AppFunctions.rsp, Some(IIN(0, 0)), List(headers))
    toHex(apdu.toBytes) should equal(rsp)
    val parsed = Apdu(fromHex(rsp))
    parsed should equal(apdu)
  }

  test("Read 2 byte count header w/ 1 elements") {
    val rsp = "C0 81 00 00 01 02 08 01 00 FF"
    val ctrl = AppCtrl(true, true, false, false, 0)
    val headers = TwoByteCountHeader(Group1Var2, UInt16LE(1), Group1Var2(0xFF.toByte))
    val apdu = Apdu(ctrl, AppFunctions.rsp, Some(IIN(0, 0)), List(headers))
    toHex(apdu.toBytes) should equal(rsp)
    val parsed = Apdu(fromHex(rsp))
    parsed should equal(apdu)
  }

  test("Read Group1 Var1") {
    val rsp = "C0 81 00 00 01 01 07 01 FF"
    val ctrl = AppCtrl(true, true, false, false, 0)
    val headers = OneByteCountHeader(Group1Var1, UInt8(1), List(0xFF.toByte))
    val apdu = Apdu(ctrl, AppFunctions.rsp, Some(IIN(0, 0)), List(headers))
    toHex(apdu.toBytes) should equal(rsp)
    val parsed = Apdu(fromHex(rsp))
    parsed should equal(apdu)
  }
}