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
package com.automatak.aegis.dnp3.phys

import java.nio.ByteBuffer
import java.nio.channels.{ Channels, SocketChannel }

import com.automatak.aegis.japi.phys.ReadableWritableChannel

import scala.annotation.tailrec

final class SocketReadWriteChannel(channel: SocketChannel) extends ReadableWritableChannel {

  val wrappedChannel = Channels.newChannel(channel.socket().getInputStream)

  @tailrec
  override def write(buffer: ByteBuffer): Unit = {
    if (buffer.remaining() > 0) {
      channel.write(buffer)
      write(buffer)
    }
  }

  override def read(buffer: ByteBuffer, timeoutMs: Int): Int = {
    channel.socket().setSoTimeout(timeoutMs)
    wrappedChannel.read(buffer)
  }

  override def close(): Unit = channel.close()
}
