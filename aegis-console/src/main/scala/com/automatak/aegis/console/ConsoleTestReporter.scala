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

import java.text.SimpleDateFormat
import java.util.Date

import com.automatak.aegis.sapi.TestLogger
import org.fusesource.jansi.Ansi.Color._
import org.fusesource.jansi.{ Ansi, AnsiConsole }

object ConsoleTestReporter extends TestLogger {

  private def time(): String = defaulUTCDateFormat.format(new Date(System.currentTimeMillis())) + " - "
  private val defaulUTCDateFormat = new SimpleDateFormat("HH:mm:ss.SSS")

  private def print(fg: Ansi.Color)(msg: => String): Unit =
    AnsiConsole.out.print(Ansi.ansi().fg(fg).a(msg).reset())

  private def printLine(fg: Ansi.Color)(msg: => String): Unit = {
    print(WHITE)(time())
    AnsiConsole.out.println(Ansi.ansi().fg(fg).a(msg).reset())
  }

  def success(msg: => String): Unit = printLine(GREEN)(msg)
  def info(msg: => String) = printLine(WHITE)(msg)
  def warn(msg: => String) = printLine(WHITE)(msg)
  def error(msg: => String) = printLine(RED)(msg)
  def receive(msg: => String): Unit = printLine(YELLOW)(msg)
  def transmit(msg: => String): Unit = printLine(CYAN)(msg)

}

