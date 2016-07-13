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

object Group22 extends ObjectGroup {
  def objects = List(Group22Var1, Group22Var2, Group22Var3, Group22Var4, Group22Var5, Group22Var6, Group22Var7, Group22Var8)
  def group: Byte = 22
  def typ: ObjectType = Event
}

object Group22Var1 extends FixedSizeGroupVariation(Group22, 1, 5) with EventGroupVariation
object Group22Var2 extends FixedSizeGroupVariation(Group22, 2, 3) with EventGroupVariation
object Group22Var3 extends FixedSizeGroupVariation(Group22, 3, 5) with EventGroupVariation
object Group22Var4 extends FixedSizeGroupVariation(Group22, 4, 3) with EventGroupVariation
object Group22Var5 extends FixedSizeGroupVariation(Group22, 5, 11) with EventGroupVariation
object Group22Var6 extends FixedSizeGroupVariation(Group22, 6, 9) with EventGroupVariation
object Group22Var7 extends FixedSizeGroupVariation(Group22, 7, 11) with EventGroupVariation
object Group22Var8 extends FixedSizeGroupVariation(Group22, 8, 9) with EventGroupVariation