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
package com.automatak.aegis

package object sapi {

  // sapi level implicits

  implicit def convertSerializableToBytesToBytes(s: SerializableToBytes) = s.toBytes

  implicit def convertByteToBytes(b: Byte) = Array(b)

  implicit def convertUnderlyingToValue[A](a: Underlying[A]) = a.value

  implicit def convertShortToFuzz4sRichShort(i: Short) = new Fuzz4sRichShort(i)
  implicit def convertShortToFuzz4sRichInt(i: Int) = new Fuzz4sRichInt(i)
  implicit def convertShortToFuzz4sRichLong(i: Int) = new Fuzz4sRichLong(i)

  // useful utility functions

  def assemble(arr: List[Byte]*): List[Byte] = arr.foldLeft(List.empty[Byte])(_ ++ _)

  def join(arr: Traversable[Array[Byte]]): Array[Byte] = arr.foldLeft(Array[Byte]())(_ ++ _)

  def lines(seq: String*) = seq.foldLeft("")(_ + "\n" + _)

  def toHexString(byte: Byte) = "0x" + toHex(byte)

  def toHex(byte: Byte): String = String.format("%02X", java.lang.Byte.valueOf(byte))

  def toHex(int: Int, prefix: Boolean): String = {
    val hex = String.format("%04X", java.lang.Integer.valueOf(int))
    if (prefix) "0x" + hex else hex
  }

  def toHex(bytes: Iterable[Byte], spaced: Boolean = true, prefix: Boolean = false): String = {
    val hex = if (spaced) bytes.map(toHex).mkString(" ") else bytes.map(toHex).mkString
    if (prefix) "0x" + hex else hex
  }

  def fromHexByte(s: String): Result[Byte] = {
    Result(java.lang.Integer.parseInt(s, 16)).flatMap { num =>
      if (num > 255 || num < 0) Failure("number out of Byte range: " + num)
      else Success(num.toByte)
    }
  }

  def fromHex(hex: String): List[Byte] = {
    val cleanInput = hex.replaceAll("\\s|\\n", "")
    assert((cleanInput.length % 2) == 0)
    cleanInput.sliding(2, 2).map(s => java.lang.Integer.parseInt(s, 16).toByte).toList
  }

}