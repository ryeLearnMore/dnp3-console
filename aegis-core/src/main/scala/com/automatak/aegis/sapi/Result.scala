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

object Result {
  def apply[A](fun: => A): Result[A] = {
    try { Success(fun) }
    catch { case ex: Exception => Failure(ex) }
  }
}

sealed trait Result[+A] {
  def map[B](convert: A => B): Result[B]
  def flatMap[B](convert: A => Result[B]): Result[B]
  def foreach[B](fun: A => B): Unit
  def asOption: Option[Throwable]
}

final case class Success[A](value: A) extends Result[A] {
  override def map[B](convert: A => B) = Result(convert(value))
  override def flatMap[B](convert: A => Result[B]): Result[B] = {
    try {
      convert(value)
    } catch {
      case ex: Exception => Failure(ex)
    }
  }
  override def foreach[B](fun: A => B) = fun(value)
  override def asOption: Option[Throwable] = None
}

object Failure {
  def apply(msg: String): Failure = Failure(new Exception(msg))

  def exceptions(ts: Traversable[Throwable]): Failure = {
    Failure("Multiple failures detected: " + System.lineSeparator() + ts.mkString(System.lineSeparator()))
  }

  def failures(fs: Traversable[Failure]): Failure = {
    Failure("Multiple failures detected: " + System.lineSeparator() + fs.mkString(System.lineSeparator()))
  }
}

final case class Failure(ex: Throwable) extends Result[Nothing] {
  override def map[B](convert: Nothing => B): Result[B] = this
  override def flatMap[B](convert: Nothing => Result[B]) = this
  override def foreach[B](fun: Nothing => B) = {}
  override def asOption: Option[Throwable] = Some(ex)
}