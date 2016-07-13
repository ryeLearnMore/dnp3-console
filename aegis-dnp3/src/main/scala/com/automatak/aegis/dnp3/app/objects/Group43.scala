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

object Group43 extends ObjectGroup {
  def objects = List(Group43Var1, Group43Var2, Group43Var3, Group43Var4, Group43Var5, Group43Var6, Group43Var7, Group43Var8)
  def group: Byte = 43
  def typ: ObjectType = Event
}

object Group43Var1 extends FixedSizeGroupVariation(Group43, 1, 5) with EventGroupVariation
object Group43Var2 extends FixedSizeGroupVariation(Group43, 2, 3) with EventGroupVariation
object Group43Var3 extends FixedSizeGroupVariation(Group43, 3, 11) with EventGroupVariation
object Group43Var4 extends FixedSizeGroupVariation(Group43, 4, 9) with EventGroupVariation
object Group43Var5 extends FixedSizeGroupVariation(Group43, 5, 5) with EventGroupVariation
object Group43Var6 extends FixedSizeGroupVariation(Group43, 6, 9) with EventGroupVariation
object Group43Var7 extends FixedSizeGroupVariation(Group43, 7, 11) with EventGroupVariation
object Group43Var8 extends FixedSizeGroupVariation(Group43, 8, 15) with EventGroupVariation