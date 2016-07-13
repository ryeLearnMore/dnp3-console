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

object Group42 extends ObjectGroup {
  def objects = List(Group42Var1, Group42Var2, Group42Var3, Group42Var4, Group42Var5, Group42Var6, Group42Var7, Group42Var8)
  def group: Byte = 42
}

object Group42Var1 extends FixedSizeGroupVariation(Group42, 1, 5) with EventGroupVariation
object Group42Var2 extends FixedSizeGroupVariation(Group42, 2, 3) with EventGroupVariation
object Group42Var3 extends FixedSizeGroupVariation(Group42, 3, 11) with EventGroupVariation
object Group42Var4 extends FixedSizeGroupVariation(Group42, 4, 9) with EventGroupVariation
object Group42Var5 extends FixedSizeGroupVariation(Group42, 5, 5) with EventGroupVariation
object Group42Var6 extends FixedSizeGroupVariation(Group42, 6, 9) with EventGroupVariation
object Group42Var7 extends FixedSizeGroupVariation(Group42, 7, 11) with EventGroupVariation
object Group42Var8 extends FixedSizeGroupVariation(Group42, 8, 15) with EventGroupVariation
