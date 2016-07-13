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

import com.automatak.aegis.dnp3.app.Apdu
import com.automatak.aegis.dnp3.link.Lpdu
import com.automatak.aegis.dnp3.transport.Transport
import com.automatak.aegis.sapi._

/**
 * Specialized trait for DNP3 reporting
 */
trait DNP3Logger extends TestLogger {

  def receiveLPDU(lpdu: Lpdu): Unit = receive("<- " + lpdu.toString)

  def receiveTPDU(header: Transport.Header, data: List[Byte]): Unit = receive("<~ " + header.toString)

  def receiveAPDU(apdu: Apdu): Unit = {
    receive("<= " + apdu.toString)
    apdu.headers.foreach(header => receive("<= " + header.toString))
  }

  def transmitPhys(bytes: List[Byte]): Unit = transmit(">> " + toHex(bytes))

  def transmitLPDU(lpdu: Lpdu): Unit = transmit("-> " + lpdu.toString)

  def transmitTPDU(header: Transport.Header, data: List[Byte]): Unit = transmit("~> " + header.toString + " payload size: " + data.size)

  def transmitAPDU(apdu: Apdu): Unit = {
    transmit("=> " + apdu.toString)
    transmit("=> " + toHex(apdu.toBytes))
  }

}

/**
 * Proxy class that maps calls
 * @param reporter Standard report trait
 */
class DNP3LoggerAdapter(reporter: TestLogger) extends DNP3Logger {

  def success(msg: => String): Unit = reporter.success(msg)
  def info(msg: => String): Unit = reporter.info(msg)
  def warn(msg: => String): Unit = reporter.warn(msg)
  def error(msg: => String): Unit = reporter.error(msg)
  def receive(msg: => String): Unit = reporter.receive(msg)
  def transmit(msg: => String): Unit = reporter.transmit(msg)

}