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

import annotation.tailrec

import com.automatak.aegis.sapi.{ TestLogger, UInt16LE, UInt8 }
import com.automatak.aegis.dnp3.link.{ Lpdu, LinkCtrl, LinkHeader }
import com.automatak.aegis.dnp3.app.{ IIN, AppFunctions, AppCtrl, Apdu }

class TargetMonitorException(msg: String) extends Exception(msg)

class FailureDetector(dest: UInt16LE, src: UInt16LE, isMaster: Boolean) {

  def requestLinkStates() = Lpdu(LinkHeader(UInt8(5), LinkCtrl(isMaster, true, false, false, Lpdu.REQUEST_LINK_STATES), dest, src))
  def linkStatusRsp() = Lpdu(LinkHeader(UInt8(5), LinkCtrl(!isMaster, false, false, false, Lpdu.LINK_STATUS), src, dest))

  val challenge = requestLinkStates()
  val response = linkStatusRsp()

  val ack = Lpdu(LinkHeader(UInt8(5), LinkCtrl(false, false, false, false, Lpdu.ACK), dest, src))
  val linkStatus = Lpdu(LinkHeader(UInt8(5), LinkCtrl(false, false, false, false, Lpdu.LINK_STATUS), dest, src))

  def handshakeWithMaster(lpdu: Lpdu, driver: TestDriver): Unit = {

    def respondNull(seq: Byte) = {
      val rsp = Apdu(AppCtrl(true, true, false, false, seq), AppFunctions.rsp, Some(IIN.empty), Nil)
      driver.writeAPDU(rsp)
    }

    lpdu match {
      case Lpdu(LinkHeader(l_, LinkCtrl(true, true, _, _, Lpdu.UNCONFIRMED_USER_DATA), src, dest), tpdu :: apdu) =>
        respondNull(Apdu.parse(apdu, false).ctrl.seq)
      case Lpdu(LinkHeader(_, LinkCtrl(true, true, _, _, Lpdu.CONFIRMED_USER_DATA), src, dest), tpdu :: apdu) =>
        driver.writeLPDU(ack)
        respondNull(Apdu.parse(apdu, false).ctrl.seq)
      case Lpdu(LinkHeader(_, LinkCtrl(true, true, _, _, Lpdu.RESET_LINK_STATES), src, dest), _) =>
        driver.writeLPDU(ack)
      case Lpdu(LinkHeader(_, LinkCtrl(true, true, _, _, Lpdu.REQUEST_LINK_STATES), src, dest), _) =>
        driver.writeLPDU(linkStatus)
      case _ =>
    }
  }

  def detectFailure(driver: TestDriver, reporter: TestLogger, retries: Int, continuePacketCount: Int): Unit = {

    @tailrec
    def recurse(attempts: Int): Unit = {

      @tailrec
      def verifyLinkStatus(validPacketsReceived: Int): Boolean = {
        if (validPacketsReceived >= continuePacketCount) {
          true
        } else {
          driver.readLPDU() match {
            case Some(lpdu) =>
              if (lpdu == response) true
              else {
                // some masters will not process unsolicited messages until you handshake with them in various ways
                // This little chunk of code answers master requests with a NULL respond and correct sequence number
                if (!isMaster) handshakeWithMaster(lpdu, driver)
                verifyLinkStatus(validPacketsReceived + 1)
              }
            case None => false
          }
        }
      }

      if (attempts <= 0) {
        throw new TargetMonitorException("Failure while reading link status")
      } else {
        driver.writeLPDU(challenge)
        if (!verifyLinkStatus(0)) {
          reporter.warn("Retrying link status: " + attempts + " attempts remaining")
          recurse(attempts - 1)
        }
      }
    }

    recurse(retries + 1)
  }

}