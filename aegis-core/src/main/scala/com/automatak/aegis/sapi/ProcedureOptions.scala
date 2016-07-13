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

object ProcedureOptions {

  def attempt[A](f: => A): Result[A] = {
    try {
      Success(f)
    } catch {
      case ex: Exception => Failure(ex)
    }
  }

  def parseInt(value: String): Result[Int] = Result(Integer.parseInt(value))

}

trait Dictionary {

  case class Value(key: String, value: Option[String])

  def values: Iterable[Value]
  def get(key: String): Option[Option[String]]
}

object Bounds {

  def render(b: Bounds): String = b match {
    case Bounds(None, None) => ""
    case Bounds(Some(min), Some(max)) => "[" + min + ", " + max + "]"
    case Bounds(Some(min), None) => "[min=" + min + "]"
    case Bounds(None, Some(max)) => "[max=" + max + "]"
  }

  def Empty: Bounds = Bounds(None, None)

}

case class Bounds(lower: Option[String], upper: Option[String])

trait ProcedureOption {
  def key: String
  def hasValue: Boolean
  def description: String
  def default: Option[String]
  def bounds: Bounds
}

object ArgumentFailure {
  def apply(key: String, description: String) = Failure("Required argument not found: " + key + " (" + description + ")")
}

object FlagFailure {
  def apply(key: String, description: String) = Failure("Flag requires an arugment: " + key + " (" + description + ")")
}

case class StringOption(key: String, description: String, default: Option[String]) extends ProcedureOption {

  def bounds = Bounds.Empty

  def get(d: Dictionary): Result[String] = d.get(key) match {
    case Some(option) => option match {
      case Some(x) => Success(x)
      case None => FlagFailure(key, description)
    }
    case None => default match {
      case Some(d) => Success(d)
      case None => ArgumentFailure(key, description)
    }

  }

  def hasValue = true

}

case class BooleanFlag(key: String, description: String) extends ProcedureOption {

  def bounds = Bounds.Empty

  def default = Some("false")

  def get(d: Dictionary): Boolean = d.get(key).isDefined

  def hasValue = false
}

object IntMinMax {

  def bounds(min: Int, max: Int): IntMinMax = IntMinMax(Some(min), Some(max))
  def minOnly(min: Int): IntMinMax = IntMinMax(Some(min), None)
  def maxOnly(max: Int): IntMinMax = IntMinMax(None, Some(max))
}

case class IntMinMax(min: Option[Int], max: Option[Int]) {

  private def checkMin(num: Int): Result[Int] = min match {
    case Some(x) => {
      if (num < x) Failure("Value " + num + " is less than lower bound of " + x)
      else Success(num)
    }
    case None => Success(num)
  }

  private def checkMax(num: Int): Result[Int] = max match {
    case Some(x) => {
      if (num > x) Failure("Value " + num + " is greater than upper bound of " + x)
      else Success(num)
    }
    case None => Success(num)
  }

  def checkMinMax(num: Int): Result[Int] = {
    for {
      x <- checkMin(num)
      y <- checkMax(x)
    } yield y
  }
}

final case class IntegerOption(key: String, description: String, bound: IntMinMax, defaults: Option[Int]) extends ProcedureOption {

  def default = defaults.map(_.toString)

  def bounds = Bounds(bound.min.map(_.toString), bound.max.map(_.toString))

  def hasValue = true

  def getOption(d: Dictionary): Result[Option[Int]] = {
    d.get(key) match {
      case Some(option) => option match {
        case Some(str) => for {
          num <- Result(Integer.parseInt(str))
          validated <- bound.checkMinMax(num)
        } yield Some(validated)
        case None => FlagFailure(key, description)
      }
      case None => Success(None)
    }
  }

  def get(d: Dictionary): Result[Int] = d.get(key) match {
    case Some(option) => option match {
      case Some(str) => for {
        num <- Result(Integer.parseInt(str))
        validated <- bound.checkMinMax(num)
      } yield validated
      case None => FlagFailure(key, description)
    }
    case None => defaults match {
      case Some(d) => Success(d)
      case None => ArgumentFailure(key, description)
    }
  }

}
