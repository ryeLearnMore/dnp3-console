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

object Group21 extends ObjectGroup {
  def objects = List(
    Group21Var1,
    Group21Var2,
    Group21Var3,
    Group21Var4,
    Group21Var5,
    Group21Var6,
    Group21Var7,
    Group21Var8,
    Group21Var9,
    Group21Var10,
    Group21Var11,
    Group21Var12)
  def group: Byte = 21
  def typ: ObjectType = Static
}

object Group21Var1 extends FixedSizeGroupVariation(Group21, 1, 5) with StaticGroupVariation
object Group21Var2 extends FixedSizeGroupVariation(Group21, 2, 3) with StaticGroupVariation
object Group21Var3 extends FixedSizeGroupVariation(Group21, 3, 5) with StaticGroupVariation
object Group21Var4 extends FixedSizeGroupVariation(Group21, 4, 3) with StaticGroupVariation
object Group21Var5 extends FixedSizeGroupVariation(Group21, 5, 11) with StaticGroupVariation
object Group21Var6 extends FixedSizeGroupVariation(Group21, 6, 9) with StaticGroupVariation
object Group21Var7 extends FixedSizeGroupVariation(Group21, 7, 11) with StaticGroupVariation
object Group21Var8 extends FixedSizeGroupVariation(Group21, 8, 9) with StaticGroupVariation
object Group21Var9 extends FixedSizeGroupVariation(Group21, 9, 4) with StaticGroupVariation
object Group21Var10 extends FixedSizeGroupVariation(Group21, 10, 2) with StaticGroupVariation
object Group21Var11 extends FixedSizeGroupVariation(Group21, 11, 4) with StaticGroupVariation
object Group21Var12 extends FixedSizeGroupVariation(Group21, 12, 2) with StaticGroupVariation
