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

import com.automatak.aegis.dnp3.DNP3Logger
import com.automatak.aegis.sapi.{ UInt16LE, UInt8 }

object LinkWriter {

  def write(src: UInt16LE, dest: UInt16LE, isMaster: Boolean, reporter: DNP3Logger)(data: List[Byte])(fun: List[Byte] => Unit): Unit = {
    val ctrl = LinkCtrl(isMaster, true, false, false, Lpdu.UNCONFIRMED_USER_DATA)
    val header = LinkHeader(UInt8((5 + data.size).toShort), ctrl, dest, src)
    val lpdu = Lpdu(header, data)
    reporter.transmitLPDU(lpdu)
    fun(lpdu.toBytes)
  }

}