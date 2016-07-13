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

object AppCtrl {

  val firMask = 0x80
  val finMask = 0x40
  val conMask = 0x20
  val unsMask = 0x10
  val seqMask = 0x0F

  def nextSeq(seq: Byte): Byte = ((seq + 1) % 16).toByte

  def apply(byte: Byte): AppCtrl = {
    val fir = (byte & firMask) != 0
    val fin = (byte & finMask) != 0
    val con = (byte & conMask) != 0
    val uns = (byte & unsMask) != 0
    val seq = (byte & seqMask).toByte

    AppCtrl(fir, fin, con, uns, seq)
  }
}

case class AppCtrl(fir: Boolean, fin: Boolean, con: Boolean, uns: Boolean, seq: Byte) {
  import AppCtrl._
  import com.automatak.aegis.sapi.toHexString

  def toByte: Byte = {
    val firData = if (fir) firMask else 0
    val finData = if (fin) finMask else 0
    val conData = if (con) conMask else 0
    val unsData = if (uns) unsMask else 0
    val seqData = seq & seqMask

    (firData | finData | conData | unsData | seqData).toByte
  }

  override def toString: String =
    "fir: " + fir + " fin: " + fin + " con: " + con + " uns: " + uns + " seq: " + toHexString(seq)
}

object AppFunctions {

  val confirm: Byte = 0x00
  val read: Byte = 0x01
  val write: Byte = 0x02
  val select: Byte = 0x03
  val operate: Byte = 0x04
  val directOp: Byte = 0x05
  val directOpNoRsp: Byte = 0x06
  val freeze: Byte = 0x07
  val freezeNoRsp: Byte = 0x08
  val freezeClear: Byte = 0x9
  val freezeClearNoRsp: Byte = 0x0A
  val freezeAtTime: Byte = 0x0B
  val freezeAtTimeNoRsp: Byte = 0x0C
  val coldRestart: Byte = 0x0D
  val warmRestart: Byte = 0x0E
  val initData: Byte = 0x0F
  val initApp: Byte = 0x10
  val startApp: Byte = 0x11
  val stopApp: Byte = 0x12
  val saveConfig: Byte = 0x13
  val enableUnsol: Byte = 0x14
  val disableUnsol: Byte = 0x15
  val assignClass: Byte = 0x16
  val delayMeas: Byte = 0x17
  val recordCurrentTime: Byte = 0x18
  val openFile: Byte = 0x19
  val closeFile: Byte = 0x1A
  val deleteFile: Byte = 0x1B
  val getFileInfo: Byte = 0x1C
  val authFile: Byte = 0x1D
  val abortFile: Byte = 0x1E
  val rsp: Byte = (0x81).toByte
  val unsolRsp: Byte = (0x82).toByte

  def funcToString(func: Byte): String = map.get(func).getOrElse("Unknown")

  private val map: Map[Byte, String] = Map(
    confirm -> "Confirm",
    read -> "Read",
    write -> "Write",
    select -> "Select",
    operate -> "Operate",
    directOp -> "DirectOperate",
    directOpNoRsp -> "DirectOperateNoResponse",
    freeze -> "Freeze",
    freezeNoRsp -> "FreezeNoResponse",
    freezeClear -> "FreezeClear",
    freezeClearNoRsp -> "FreezeClearNoResponse",
    freezeAtTime -> "FreezeAtTime",
    freezeAtTimeNoRsp -> "FreezeAtTimeNoResponse",
    coldRestart -> "ColdRestart",
    warmRestart -> "WarmRestart",
    initData -> "InitData",
    initApp -> "InitApp",
    startApp -> "StartApp",
    stopApp -> "StopApp",
    saveConfig -> "SaveConfig",
    enableUnsol -> "EnableUnsol",
    disableUnsol -> "DisableUnsol",
    assignClass -> "AssignClass",
    delayMeas -> "DelayMeas",
    recordCurrentTime -> "RecordCurrentTime",
    openFile -> "OpenFile",
    closeFile -> "CloseFile",
    deleteFile -> "DeleteFile",
    getFileInfo -> "GetFileInfo",
    authFile -> "AuthFile",
    abortFile -> "AbortFile",
    rsp -> "Response",
    unsolRsp -> "UnsolicitedResponse")

  val codes = List(
    confirm,
    read,
    write,
    select,
    operate,
    directOp,
    directOpNoRsp,
    freeze,
    freezeNoRsp,
    freezeClear,
    freezeClearNoRsp,
    freezeAtTime,
    freezeAtTimeNoRsp,
    //coldRestart,
    //warmRestart,
    initData,
    initApp,
    startApp,
    stopApp,
    saveConfig,
    enableUnsol,
    disableUnsol,
    assignClass,
    delayMeas,
    recordCurrentTime,
    openFile,
    closeFile,
    deleteFile,
    getFileInfo,
    authFile,
    abortFile)

}