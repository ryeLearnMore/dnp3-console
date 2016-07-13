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

import java.nio.ByteBuffer

import com.automatak.aegis.dnp3.app.Apdu
import com.automatak.aegis.dnp3.link._
import com.automatak.aegis.dnp3.transport.{ TpduWriter, TransportReader }
import com.automatak.aegis.japi.phys.ReadableWritableChannel

trait TestDriver {

  def writePhys(bytes: List[Byte]): Unit
  def writeLPDU(lpdu: Lpdu): Unit
  def writeTPDU(tpdu: List[Byte]): Unit
  def writeAPDU(apdu: Apdu): Unit

  def readLPDU(): Option[Lpdu]
  def readAPDU(): Option[Apdu]

}

class DefaultTestDriver(reporter: DNP3Logger, channel: ReadableWritableChannel, config: DNP3Options) extends TestDriver {

  private val parser = new LpduParser
  private val lpduReader = new ValidatingLpduReader(this, parser, config.src, config.dest)(writeLPDU)

  def writePhys(bytes: List[Byte]): Unit = writeToChannel(bytes)

  private def writeToChannel(bytes: List[Byte]) = {
    reporter.transmitPhys(bytes)
    channel.write(ByteBuffer.wrap(bytes.toArray))
  }

  override def writeAPDU(apdu: Apdu): Unit = {
    reporter.transmitAPDU(apdu)
    TpduWriter.write(apdu.toBytes, 0, Some(reporter)) { tpdu =>
      LinkWriter.write(config.src, config.dest, true, reporter)(tpdu)(writeToChannel)
    }
  }

  override def writeTPDU(tpdu: List[Byte]): Unit = {
    TpduWriter.write(tpdu, 0, Some(reporter)) { data =>
      LinkWriter.write(config.src, config.dest, true, reporter)(tpdu)(writeToChannel)
    }
  }

  override def writeLPDU(lpdu: Lpdu): Unit = {
    reporter.transmitLPDU(lpdu)
    writeToChannel(lpdu.toBytes)
  }

  override def readAPDU(): Option[Apdu] = {
    try {
      val apdu = TransportReader.read(lpduReader, Some(reporter))(config.appTimeout).map(Apdu.apply)
      apdu.foreach(reporter.receiveAPDU)
      apdu
    } catch {
      case ex: Exception =>
        reporter.error("Error parsing APDU: " + ex)
        None
    }
  }

  override def readLPDU(): Option[Lpdu] = parser.read(channel: ReadableWritableChannel, config.linkTimeout, Some(reporter))

}