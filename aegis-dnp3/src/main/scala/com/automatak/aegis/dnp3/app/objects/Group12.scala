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
package com.automatak.aegis.dnp3.app.objects

import com.automatak.aegis.dnp3.app._
import com.automatak.aegis.sapi.{ SerializableToBytes, UInt32LE }

object Group12 extends ObjectGroup {
  def objects = List(Group12Var1, Group12Var2, Group12Var3)
  def group: Byte = 12
  def typ: ObjectType = Command

  object FuncCodes {
    val pulse = 0x01.toByte
    val latchOn = 0x03.toByte
    val latchOff = 0x04.toByte
    val pulseClose = 0x41.toByte
    val pulseTrip = 0x81.toByte

    case class Description(byte: Byte, desc: String)
    val descriptions = List(
      Description(pulse, "pulse"),
      Description(latchOn, "latchOn"),
      Description(latchOff, "latchOn"),
      Description(pulseClose, "pulseClose"),
      Description(pulseTrip, "pulseTrip"))
  }
}

case class Group12Var1(code: Byte, count: Byte, onTime: UInt32LE, offTime: UInt32LE, status: Byte) extends SerializableToBytes {
  override def toBytes = code :: count :: onTime.toBytes ::: offTime.toBytes ::: List(status)
}

object Group12Var1 extends FixedSizeGroupVariation(Group12, 1, 11) with CommandGroupVariation
object Group12Var2 extends FixedSizeGroupVariation(Group12, 2, 11) with CommandGroupVariation
object Group12Var3 extends BasicGroupVariation(Group12, 3) with SingleBitfield with CommandGroupVariation
