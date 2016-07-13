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

object Group60 extends ObjectGroup {
  def objects = List(Group60Var1, Group60Var2, Group60Var3, Group60Var4)
  def group: Byte = 60
  def typ: ObjectType = Information
}

sealed trait ClassGroupVariation extends InformationGroupVariation {
  def clazz: Int
  def synonym: String = "Class" + clazz
}
sealed trait EventClassGroupVariation extends ClassGroupVariation

object Group60Var1 extends SizelessGroupVariation(Group60, 1) with ClassGroupVariation {
  def clazz = 0
}
object Group60Var2 extends SizelessGroupVariation(Group60, 2) with EventClassGroupVariation {
  def clazz = 1
}
object Group60Var3 extends SizelessGroupVariation(Group60, 3) with EventClassGroupVariation {
  def clazz = 2
}
object Group60Var4 extends SizelessGroupVariation(Group60, 4) with EventClassGroupVariation {
  def clazz = 3
}