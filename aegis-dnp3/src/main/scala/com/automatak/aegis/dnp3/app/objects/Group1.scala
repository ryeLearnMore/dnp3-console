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
import com.automatak.aegis.sapi.SerializableToBytes

object Group1 extends ObjectGroup {
  def objects = List(Group1Var0, Group1Var2, Group1Var1)
  def group: Byte = 1
  def typ: ObjectType = Static
}

object Group1Var0 extends SizelessGroupVariation(Group1, 0) with StaticGroupVariation

object Group1Var1 extends BasicGroupVariation(Group1, 1) with SingleBitfield with StaticGroupVariation

object Group1Var2 extends FixedSizeGroupVariation(Group1, 2, 1) with StaticGroupVariation

case class Group1Var2(flags: Byte) extends SerializableToBytes {
  override def toBytes: List[Byte] = List(flags)
}
