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

object Integers {
  val minUInt8: Short = 0
  val maxUInt8: Short = 255

  val minUInt16: Int = 0
  val maxUInt16: Int = 65535

  val minUInt32: Long = 0
  val maxUInt32: Long = 4294967295L

  val minUInt48: Long = 0
  val maxUInt48: Long = 281474976710655L

  def toBytesBE(value: Long, bytes: Int): List[Byte] = {
    assert(bytes > 0)
    Range(0, bytes, 1).foldLeft(Nil: List[Byte]) { (sum, i) =>
      ((value >> i * 8) & 0xFF).toByte :: sum
    }
  }

  def toBytesLE(value: Long, bytes: Int): List[Byte] = toBytesBE(value, bytes).reverse
}

trait Underlying[A] {
  val value: A
}

object UInt8 {
  val min = UInt8(Integers.minUInt8)
  val max = UInt8(Integers.maxUInt8)

  def fromByte(byte: Byte): UInt8 = UInt8(byteToShort(byte))

  def byteToShort(byte: Byte): Short = if (byte < 0) (byte.toShort + 256).toShort else byte.toShort

}

case class UInt8(value: Short) extends SerializableToBytes with Underlying[Short] {

  assert(value >= Integers.minUInt16)
  assert(value <= Integers.maxUInt16)

  def toBytes = List(value.toByte)
}

case class SInt8(value: Byte) extends SerializableToBytes with Underlying[Byte] {
  def toBytes = List(value)
}

object UInt16LE {
  val min = UInt16LE(Integers.minUInt16)
  val max = UInt16LE(Integers.maxUInt16)

  def from(short: Short): UInt16LE = {
    val i = short.toInt
    if (i < 0) UInt16LE(i + 65536) else UInt16LE(i)
  }

  def apply(lsb: Byte, msb: Byte): UInt16LE = {
    val b1 = UInt8.byteToShort(lsb)
    val b2 = UInt8.byteToShort(msb)

    UInt16LE((b2.toInt << 8) | b1.toInt)
  }
}

object UInt16BE {
  val min = UInt16BE(Integers.minUInt16)
  val max = UInt16BE(Integers.maxUInt16)
}

abstract class UInt16(val value: Int) extends Underlying[Int] {

  assert(value >= Integers.minUInt16)
  assert(value <= Integers.maxUInt16)
}

abstract class SInt16(val value: Short) extends Underlying[Short]

case class SInt16LE(i: Short) extends SInt16(i) with SerializableToBytes { def toBytes = Integers.toBytesLE(i, 2) }
case class SInt16BE(i: Short) extends SInt16(i) with SerializableToBytes { def toBytes = Integers.toBytesBE(i, 2) }
case class UInt16LE(i: Int) extends UInt16(i) with SerializableToBytes { def toBytes = Integers.toBytesLE(i, 2) }
case class UInt16BE(i: Int) extends UInt16(i) with SerializableToBytes { def toBytes = Integers.toBytesBE(i, 2) }

object UInt32LE {
  val min = UInt32LE(Integers.minUInt32)
  val max = UInt32LE(Integers.maxUInt32)

  def apply(b1: Byte, b2: Byte, b3: Byte, b4: Byte): UInt32LE = {
    UInt32LE((b4.toInt << 24) | (b3.toInt << 16) | (b2.toInt << 8) | b1.toInt)
  }
}

object UInt32BE {
  val min = UInt32BE(Integers.minUInt32)
  val max = UInt32BE(Integers.maxUInt32)
}

abstract class UInt32(val value: Long) extends Underlying[Long] {
  assert(value >= Integers.minUInt32)
  assert(value <= Integers.maxUInt32)
}

abstract class SInt32(val value: Int) extends Underlying[Int]

case class SInt32LE(i: Int) extends SInt32(i) with SerializableToBytes { def toBytes = Integers.toBytesLE(i, 4) }
case class SInt32BE(i: Int) extends SInt32(i) with SerializableToBytes { def toBytes = Integers.toBytesBE(i, 4) }
case class UInt32LE(i: Long) extends UInt32(i) with SerializableToBytes { def toBytes = Integers.toBytesLE(i, 4) }
case class UInt32BE(i: Long) extends UInt32(i) with SerializableToBytes { def toBytes = Integers.toBytesBE(i, 4) }

abstract class UInt48(val value: Long) extends Underlying[Long] {
  assert(value >= Integers.minUInt48)
  assert(value <= Integers.maxUInt48)
}

case class UInt48LE(i: Long) extends UInt48(i) with SerializableToBytes { def toBytes = Integers.toBytesLE(i, 6) }
