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

object Group20 extends ObjectGroup {
  def objects = List(Group20Var1, Group20Var2, Group20Var3, Group20Var4, Group20Var5, Group20Var6, Group20Var7, Group20Var8)
  def group: Byte = 20
  def typ: ObjectType = Static
}

object Group20Var1 extends FixedSizeGroupVariation(Group20, 1, 5) with StaticGroupVariation
object Group20Var2 extends FixedSizeGroupVariation(Group20, 2, 3) with StaticGroupVariation
object Group20Var3 extends FixedSizeGroupVariation(Group20, 3, 5) with StaticGroupVariation
object Group20Var4 extends FixedSizeGroupVariation(Group20, 4, 3) with StaticGroupVariation
object Group20Var5 extends FixedSizeGroupVariation(Group20, 5, 4) with StaticGroupVariation
object Group20Var6 extends FixedSizeGroupVariation(Group20, 6, 2) with StaticGroupVariation
object Group20Var7 extends FixedSizeGroupVariation(Group20, 7, 4) with StaticGroupVariation
object Group20Var8 extends FixedSizeGroupVariation(Group20, 8, 2) with StaticGroupVariation
