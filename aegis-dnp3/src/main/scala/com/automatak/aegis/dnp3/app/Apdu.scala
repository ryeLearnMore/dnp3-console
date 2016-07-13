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
package com.automatak.aegis.dnp3.app

import com.automatak.aegis.sapi._

import scala.annotation.tailrec

object Apdu {

  private def getIIN(bytes: List[Byte]): (IIN, List[Byte]) = bytes match {
    case lsb :: msb :: remainder =>
      (IIN(lsb, msb), remainder)
    case _ =>
      throw new Exception("Insufficient bytes for IIN")
  }

  private def getObjectHeaders(bytes: List[Byte]): List[ObjectHeader] = {

    @tailrec
    def recurse(headers: List[ObjectHeader], remainder: List[Byte]): List[ObjectHeader] = {
      remainder match {
        case Nil => headers
        case group :: variation :: qualifier :: remainder =>
          ObjectHeaderParser(qualifier) match {
            case Some(parser) =>
              val (header, bytes) = parser.parse(group, variation, remainder)
              recurse(header :: headers, bytes)
            case None =>
              throw new Exception("Unknown qualifier code: " + qualifier)
          }
        case _ =>
          throw new Exception("Insufficient bytes for object header: " + remainder.map(_.toInt))
      }
    }

    recurse(Nil, bytes).reverse
  }

  def apply(bytes: List[Byte]): Apdu = parse(bytes, true)

  def parse(bytes: List[Byte], parseHeaders: Boolean = true): Apdu = {

    def headers(bytes: List[Byte]) = if (parseHeaders) getObjectHeaders(bytes) else Nil

    def getApduWithIIN(ctrl: AppCtrl, func: Byte, bytes: List[Byte]): Apdu = {
      val (iin, remainder) = getIIN(bytes)
      Apdu(ctrl, func, Some(iin), headers(remainder))
    }

    bytes match {
      case ctrl :: func :: remainder =>
        val control = AppCtrl(ctrl)
        func match {
          case AppFunctions.rsp => getApduWithIIN(control, func, remainder)
          case AppFunctions.unsolRsp => getApduWithIIN(control, func, remainder)
          case _ => Apdu(control, func, None, headers(remainder))
        }
      case _ =>
        throw new Exception("Insufficient bytes for Apdu")
    }
  }
}

case class Apdu(ctrl: AppCtrl, function: Byte, iin: Option[IIN], headers: List[ObjectHeader]) extends SerializableToBytes {

  override def toBytes = {
    ctrl.toByte :: function :: (iin.map(_.toBytes).getOrElse(Nil) ::: headers.flatMap(_.toBytes))
  }

  override def toString(): String = {
    val s1 = ctrl.toString + " func: " + AppFunctions.funcToString(function) + "(" + toHexString(function) + ")"
    iin match {
      case Some(x) => s1 + " " + x.toString
      case None => s1
    }
  }

}

