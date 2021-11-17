/*
 * Copyright 2016 Heiko Seeberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.heikoseeberger.sbtgroll

import java.io.File
import sbt.{ AutoPlugin, Command, Keys, PluginTrigger, State, plugins, settingKey }
import scala.reflect.{ ClassTag, classTag }

object GrollKey {

  final val GrollConfigFileDefault = ".sbt-groll.conf"

  final val GrollHistoryRefDefault = "main"

  final val GrollWorkingBranchDefault = "groll"

  val grollConfigFile = settingKey[File](
    s"""The configuration file for sbt-groll; "~/$GrollConfigFileDefault" by default"""
  )

  val grollHistoryRef = settingKey[String](
    s"""The ref (commit id, branch or tag) used for the Git history; "$GrollHistoryRefDefault" by default"""
  )

  val grollWorkingBranch = settingKey[String](
    s"""The working branch used by sbt-groll; "$GrollWorkingBranchDefault" by default"""
  )
}

object GrollPlugin extends AutoPlugin {

  val autoImport: GrollKey.type =
    GrollKey

  import autoImport._

  override def trigger: PluginTrigger =
    allRequirements

  override def requires: plugins.JvmPlugin.type =
    plugins.JvmPlugin

  override def projectSettings =
    List(
      Keys.commands += grollCommand,
      grollConfigFile    := new File(System.getProperty("user.home"), GrollConfigFileDefault),
      grollHistoryRef    := GrollHistoryRefDefault,
      grollWorkingBranch := GrollWorkingBranchDefault
    )

  private def grollCommand = Command("groll")(parser)(Groll.apply)

  private def parser(state: State) = {
    import GrollArg._
    import sbt.complete.DefaultParsers._
    def arg(grollArg: GrollArg) = (Space ~> grollArg.toString.decapitalize).map(_ => grollArg)
    def opt[A <: GrollArg: ClassTag](ctor: String => A) = {
      val name = classTag[A].runtimeClass.getSimpleName
      (Space ~> name.decapitalize ~> "=" ~> NotQuoted).map(ctor)
    }
    arg(Show) |
    arg(List) |
    arg(Next) |
    arg(Prev) |
    arg(Head) |
    arg(Initial) |
    opt(Move) |
    opt(Push) |
    arg(Help)
  }
}
