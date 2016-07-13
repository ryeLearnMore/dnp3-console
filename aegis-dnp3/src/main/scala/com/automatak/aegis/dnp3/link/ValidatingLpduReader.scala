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

import com.automatak.aegis.dnp3.{ DNP3Logger, TestDriver }
import com.automatak.aegis.sapi.{ UInt16LE, UInt8 }

import scala.annotation.tailrec

class ValidatingLpduReader(driver: TestDriver, parser: LpduParser, local: UInt16LE, remote: UInt16LE)(writer: Lpdu => Unit) extends LpduDataReader {

  private val ack = Lpdu(LinkHeader(UInt8(5), LinkCtrl(true, false, false, false, Lpdu.ACK), remote, local))

  def read(timeoutMs: Long, reporter: Option[DNP3Logger] = None): Option[List[Byte]] = {

    val end = System.currentTimeMillis() + timeoutMs

    def validate(lpdu: Lpdu): Option[List[Byte]] = {
      if (lpdu.header.dest == local && lpdu.header.src == remote) lpdu.header.ctrl.func match {
        case Lpdu.CONFIRMED_USER_DATA =>
          writer(ack)
          Some(lpdu.data)
        case Lpdu.UNCONFIRMED_USER_DATA =>
          Some(lpdu.data)
        case _ =>
          reporter.foreach(_.error("Ignoring unexpected lpdu: " + lpdu))
          None
      }
      else {
        reporter.foreach(_.error("Ignoring lpdu with bad addressing: " + lpdu))
        None
      }
    }

    @tailrec
    def recurse(): Option[List[Byte]] = {
      val now = System.currentTimeMillis()
      val remain = (end - now).toInt
      if (remain > 0) driver.readLPDU() match {
        case Some(lpdu) => validate(lpdu) match {
          case Some(bytes) => Some(bytes)
          case None => recurse()
        }
        case None =>
          None
      }
      else None
    }

    recurse()
  }

}
