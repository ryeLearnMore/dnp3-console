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
package com.automatak.aegis.sapi

import java.util.Random

trait RandomGenerator {
  def bytes(num: Int): List[Byte]
}

trait RandomGeneratorFactory {
  def apply(): RandomGenerator
}

case class SeededRandomFactory(seed: Long = 0) extends RandomGeneratorFactory {

  private val rand = new Random(seed)

  def apply = new RandomGenerator {
    def bytes(num: Int): List[Byte] = {
      if (num <= 0) Nil
      else {
        val bytes = new Array[Byte](num)
        rand.nextBytes(bytes)
        bytes.toList
      }
    }
  }

}

case class FixedRandomFactory(repeat: Byte) extends RandomGeneratorFactory {
  def apply = new RandomGenerator {
    def bytes(num: Int): List[Byte] = FixedBytes.repeat(repeat)(num)
  }
}