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

import com.automatak.aegis.dnp3._
import com.automatak.aegis.dnp3.phys.{ TcpClientChannelFactory, TcpServerChannelFactory }
import com.automatak.aegis.japi.phys.ChannelFactory
import com.automatak.aegis.sapi.{ BooleanFlag, IntegerOption, StringOption, _ }

/**
 * Specifies options and how to read the basic configuration
 */
object Configuration {

  case class Resources(factory: ChannelFactory)
  case class Options(help: Boolean, resources: Resources, dnp3: DNP3Options)

  object Options {
    val host = StringOption("host", "IP address for client connection", Some("127.0.0.1"))
    val port = IntegerOption("port", "Port to connect or listen on", IntMinMax.bounds(0, 65535), Some(20000))
    val listen = BooleanFlag("listen", "Listens on the specified port instead of connecting")
    val help = BooleanFlag("help", "Prints help information")
  }

  /**
   * Tells the outside inputs it needs to run
   * @return
   */
  def options: List[ProcedureOption] = {
    import Options._
    List(host, port, listen, help) ::: DNP3Options.asList
  }

  def read(d: Dictionary, logger: TestLogger): Result[Options] = for {
    res <- getResources(d, logger)
    help <- Success(Options.help.get(d))
    dnp3 <- DNP3Options.readConfig(d)
  } yield Options(help, res, dnp3)

  private def getResources(d: Dictionary, logger: TestLogger): Result[Resources] = {
    for {
      host <- Options.host.get(d)
      port <- Options.port.get(d)
      listen <- Success(Options.listen.get(d))
    } yield {
      Resources(getChannelFactory(listen, host, port, logger))
    }

  }

  private def getChannelFactory(listen: Boolean, host: String, port: Int, logger: TestLogger): ChannelFactory = {
    if (listen) new TcpServerChannelFactory(port, logger)
    else new TcpClientChannelFactory(host, port, logger)
  }
}
