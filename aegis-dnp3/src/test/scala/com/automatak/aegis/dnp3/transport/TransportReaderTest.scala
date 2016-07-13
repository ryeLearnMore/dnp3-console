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

import com.automatak.aegis.dnp3.DNP3Logger
import com.automatak.aegis.dnp3.link.LpduDataReader
import com.automatak.aegis.sapi._
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers

@RunWith(classOf[JUnitRunner])
class TransportReaderTest extends FunSuite with Matchers {

  class MockLinkReader extends LpduDataReader {

    val queue = collection.mutable.Queue.empty[List[Byte]]

    def read(timeoutMs: Long, reporter: Option[DNP3Logger] = None): Option[List[Byte]] = {
      if (queue.isEmpty) None
      else Some(queue.dequeue())
    }

  }

  val data = fromHex("DE AD BE EF")
  val fullTpdu = FixedBytes.repeat(0xFF.toByte)(249)

  test("correctly parses fir-fin packet") {
    val mock = new MockLinkReader
    val readFunc = TransportReader.read(mock)(_)
    mock.queue.enqueue(Transport.function(true, true, 0) :: data)
    readFunc(500) should equal(Some(data))
  }

  test("throws on non-fir packet") {
    val mock = new MockLinkReader
    val readFunc = TransportReader.read(mock)(_)
    mock.queue.enqueue(Transport.function(false, true, 0) :: data)
    intercept[Exception](readFunc(500))
  }

  test("correctly parses 3 segment APDU") {
    val mock = new MockLinkReader
    val readFunc = TransportReader.read(mock)(_)
    mock.queue.enqueue(Transport.function(true, false, 0) :: fullTpdu)
    mock.queue.enqueue(Transport.function(false, false, 1) :: fullTpdu)
    mock.queue.enqueue(Transport.function(false, true, 2) :: data)
    readFunc(500) should equal(Some(fullTpdu ::: fullTpdu ::: data))
  }

  test("Excepts when a tpdu packing error occurs") {
    val mock = new MockLinkReader
    val readFunc = TransportReader.read(mock)(_)
    mock.queue.enqueue(Transport.function(true, false, 0) :: data)
    intercept[Exception](readFunc(500))
  }

  test("Excepts when out of sequence tpdu arrives skips when new fir arrives") {
    val mock = new MockLinkReader
    val readFunc = TransportReader.read(mock)(_)
    mock.queue.enqueue(Transport.function(true, false, 0) :: fullTpdu)
    mock.queue.enqueue(Transport.function(true, true, 4) :: List(0xFF.toByte))
    intercept[Exception](readFunc(500))
  }

}