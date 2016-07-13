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

object Group23 extends ObjectGroup {
  def objects = List(Group23Var1, Group23Var2, Group23Var3, Group23Var4, Group23Var5, Group23Var6, Group23Var7, Group23Var8)
  def group: Byte = 23
  def typ: ObjectType = Event
}

object Group23Var1 extends FixedSizeGroupVariation(Group23, 1, 5) with EventGroupVariation
object Group23Var2 extends FixedSizeGroupVariation(Group23, 2, 3) with EventGroupVariation
object Group23Var3 extends FixedSizeGroupVariation(Group23, 3, 5) with EventGroupVariation
object Group23Var4 extends FixedSizeGroupVariation(Group23, 4, 3) with EventGroupVariation
object Group23Var5 extends FixedSizeGroupVariation(Group23, 5, 11) with EventGroupVariation
object Group23Var6 extends FixedSizeGroupVariation(Group23, 6, 9) with EventGroupVariation
object Group23Var7 extends FixedSizeGroupVariation(Group23, 7, 11) with EventGroupVariation
object Group23Var8 extends FixedSizeGroupVariation(Group23, 8, 9) with EventGroupVariation
