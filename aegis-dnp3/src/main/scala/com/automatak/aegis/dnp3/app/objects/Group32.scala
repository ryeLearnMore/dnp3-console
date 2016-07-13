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

object Group32 extends ObjectGroup {
  def objects = List(Group32Var1, Group32Var2, Group32Var3, Group32Var4, Group32Var5, Group32Var6, Group32Var7, Group32Var8)
  def group: Byte = 32
  def typ: ObjectType = Event
}

object Group32Var1 extends FixedSizeGroupVariation(Group32, 1, 5) with EventGroupVariation
object Group32Var2 extends FixedSizeGroupVariation(Group32, 2, 3) with EventGroupVariation
object Group32Var3 extends FixedSizeGroupVariation(Group32, 3, 11) with EventGroupVariation
object Group32Var4 extends FixedSizeGroupVariation(Group32, 4, 9) with EventGroupVariation
object Group32Var5 extends FixedSizeGroupVariation(Group32, 5, 5) with EventGroupVariation
object Group32Var6 extends FixedSizeGroupVariation(Group32, 6, 9) with EventGroupVariation
object Group32Var7 extends FixedSizeGroupVariation(Group32, 7, 11) with EventGroupVariation
object Group32Var8 extends FixedSizeGroupVariation(Group32, 8, 15) with EventGroupVariation
