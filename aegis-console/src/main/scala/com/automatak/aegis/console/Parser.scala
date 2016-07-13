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
package com.automatak.aegis.console

import com.automatak.aegis.sapi._

import scala.annotation.tailrec

/**
 * Very simple command-line parser
 */
object Parser {

  private case class MapAsDictionary(map: Map[String, Option[String]]) extends Dictionary {
    def values: Iterable[Value] = map.map((kv) => Value(kv._1, kv._2))
    def get(key: String): Option[Option[String]] = map.get(key)
  }

  def apply(args: List[String]): Either[String, Dictionary] = {

    @tailrec
    def recurse(remainder: List[String], sum: Map[String, Option[String]]): Either[String, Dictionary] = {
      remainder match {
        case x :: xs =>
          if (x.startsWith("-")) {
            val flag = x.drop(1)
            xs match {
              case y :: ys =>
                if (y.startsWith("-")) recurse(xs, sum + (flag -> None))
                else recurse(ys, sum + (flag -> Some(y)))
              case Nil =>
                Right(MapAsDictionary(sum + (flag -> None)))
            }
          } else Left("Expected a flag, but found: " + x + ". Did you mean -" + x + "?")
        case Nil =>
          Right(MapAsDictionary(sum))
      }
    }

    recurse(args, Map.empty[String, Option[String]])
  }

}
