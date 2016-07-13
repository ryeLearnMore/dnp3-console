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

import com.automatak.aegis.dnp3.app.objects._
import com.automatak.aegis.sapi._

import scala.collection.immutable.Map

sealed trait ObjectType
case object Static extends ObjectType
case object Event extends ObjectType
case object Command extends ObjectType
case object Information extends ObjectType
case object Attribute extends ObjectType

trait ObjectGroup {
  def group: Byte
  def objects: List[GroupVariation]
}

object GroupVariation {

  val groups: List[ObjectGroup] = List(
    Group1,
    Group2,
    Group3,
    Group4,
    Group10,
    Group11,
    Group12,
    Group13,
    Group20,
    Group21,
    Group22,
    Group30,
    Group31,
    Group32,
    Group33,
    Group34,
    Group40,
    Group41,
    Group42,
    Group43,
    Group50,
    Group51,
    Group52,
    Group60,
    Group80,
    Group110,
    Group111)

  val allObjects: List[GroupVariation] = groups.map(_.objects).flatten

  val staticObjects: List[StaticGroupVariation] = {
    def getStatic(gv: GroupVariation): Option[StaticGroupVariation] = gv match {
      case gv: StaticGroupVariation => Some(gv)
      case _ => None
    }
    allObjects.flatMap(getStatic)
  }

  private val lookupTable = allObjects.foldLeft(Map.empty[Byte, Map[Byte, GroupVariation]]) { (map, gv) =>
    map.get(gv.group.group) match {
      case None => map + (gv.group.group -> Map(gv.variation -> gv))
      case Some(submap) => submap.get(gv.variation) match {
        case Some(defined) => throw new Exception("This group/variation already defined as: " + defined)
        case None =>
          map + (gv.group.group -> (submap + (gv.variation -> gv)))
      }
    }
  }

  def apply(group: Byte, variation: Byte): GroupVariation = lookupTable.get(group) match {
    case None => throw new Exception("No class info on group: " + toHexString(group) + " variation: " + toHexString(variation))
    case Some(submap) => submap.get(variation) match {
      case None => throw new Exception("No variation info on group: " + toHexString(group) + " variation: " + toHexString(variation))
      case Some(gv) => gv
    }
  }
}

trait GroupVariation extends SerializableToBytes {
  def group: ObjectGroup
  def variation: Byte
  def size(count: Int): Option[Int]
  def size: Option[Int]
  def typ: ObjectType
  override def toBytes = List(group.group, variation)
  override def toString = "Group" + group.group.toInt + "Var" + variation.toInt
}

abstract class BasicGroupVariation(val group: ObjectGroup, val variation: Byte) extends GroupVariation

trait SingleBitfield extends GroupVariation {

  override def size(count: Int): Option[Int] = {
    val div = count / 8
    val mod = count % 8
    if (mod == 0) Some(div)
    else Some(div + 1)
  }

  override def size = None //cannot be used with index headers
}

trait StaticGroupVariation extends GroupVariation {
  final def typ = Static
}

trait CTOEventGroupVariation extends EventGroupVariation

trait EventGroupVariation extends GroupVariation {
  final def typ = Event
}

trait CommandGroupVariation extends GroupVariation {
  final def typ = Command
}

trait InformationGroupVariation extends GroupVariation {
  final def typ = Information
}

abstract class FixedSizeGroupVariation(group: ObjectGroup, variation: Byte, individualSize: Int) extends BasicGroupVariation(group, variation) {
  override def size(count: Int) = Some(individualSize * count)
  override def size: Option[Int] = Some(individualSize)
}

abstract class SizelessGroupVariation(group: ObjectGroup, variation: Byte) extends BasicGroupVariation(group, variation) {
  override def size(count: Int) = None
  override def size = None
}

object MockObjectGroup extends ObjectGroup {
  def group: Byte = 255.toByte
  def objects = List(MockGroupVaration)
}

object MockGroupVaration extends SizelessGroupVariation(MockObjectGroup, 255.toByte) with InformationGroupVariation
