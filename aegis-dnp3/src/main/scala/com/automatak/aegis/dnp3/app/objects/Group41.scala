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
import com.automatak.aegis.sapi.{ SInt16LE, SerializableToBytes }

object Group41 extends ObjectGroup {
  def objects = List(Group41Var1, Group41Var2, Group41Var3, Group41Var4)
  def group: Byte = 41
  def typ: ObjectType = Command
}

object Group41Var1 extends FixedSizeGroupVariation(Group41, 1, 5) with CommandGroupVariation

case class Group41Var2(value: SInt16LE, status: Byte) extends SerializableToBytes {
  def toBytes = value.toBytes ::: List(status)
}

object Group41Var2 extends FixedSizeGroupVariation(Group41, 2, 3) with CommandGroupVariation

object Group41Var3 extends FixedSizeGroupVariation(Group41, 3, 5) with CommandGroupVariation

object Group41Var4 extends FixedSizeGroupVariation(Group41, 4, 9) with CommandGroupVariation
