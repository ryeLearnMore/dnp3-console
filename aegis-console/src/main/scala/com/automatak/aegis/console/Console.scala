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

import com.automatak.aegis.sapi.{ Success, _ }
import org.fusesource.jansi.Ansi.Color.YELLOW
import org.fusesource.jansi.{ Ansi, AnsiConsole }
import com.automatak.aegis._
import com.automatak.aegis.dnp3.DNP3LoggerAdapter
import com.automatak.aegis.dnp3.app.{ Apdu, AppCtrl, MockObjectHeader }

object Console {

  def printError(msg: String): Unit = {
    AnsiConsole.out.println(Ansi.ansi().fg(YELLOW).a(msg).reset())
  }

  def main(args: Array[String]): Unit = {

    def execute(args: Array[String]): RunResult = {
      Parser(args.toList) match {
        case Right(d) =>
          Configuration.read(d, ConsoleTestReporter) match {
            case Success(config) =>
              if (config.help) {
                printBasicUsage()
                RunSuccess
              } else {
                run(config)
              }
            case Failure(ex) =>
              ConfigError(ex.getMessage)
          }
        case Left(error) =>
          ConfigError(error)
      }
    }

    Logo.print()

    val exitcode = execute(args) match {
      case RunSuccess => 0
      case ConfigError(error: String) =>
        printError(error)
        printBasicUsage()
        1
      case TestError(error, ex) =>
        ConsoleTestReporter.error(error + ": " + ex.getMessage)
        2
    }

    sys.exit(exitcode)
  }

  def run(options: Configuration.Options): RunResult = {

    val channel = options.resources.factory.open()

    val driver = new dnp3.DefaultTestDriver(
      new DNP3LoggerAdapter(ConsoleTestReporter),
      channel,
      options.dnp3)

    val apdu = Apdu(
      AppCtrl(true, true, false, false, options.dnp3.appseq),
      options.dnp3.function,
      None,
      List(MockObjectHeader(options.dnp3.headers)))

    driver.writeAPDU(apdu)

    Thread.sleep(10000)

    RunSuccess
  }

  def printBasicUsage(): Unit = {

    println()
    println("usage: dnp3-console [flags ... ]")
    printUsage(Configuration.options)
  }

  def printUsage(list: List[ProcedureOption]): Unit = list.foreach { o =>

    val arg = if (o.hasValue) {
      o.default match {
        case Some(default) => "<arg>(" + default + ")" + Bounds.render(o.bounds)
        case None => "<arg>"
      }
    } else ""

    println(("-" + o.key).padTo(20, ' ') + arg.padTo(30, ' ') + o.description)
  }

}
