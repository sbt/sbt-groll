/*
 * Copyright 2015 Heiko Seeberger
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
import sbt.complete.Parser
import sbt.{ AutoPlugin, Command, Keys, PluginTrigger, Setting, SettingKey, State }
import scala.reflect.{ ClassTag, classTag }

object SbtGroll extends AutoPlugin {

  object autoImport {

    val configFile: SettingKey[File] =
      SettingKey[File](
        prefixed("configFile"),
        """The configuration file for sbt-groll; "~/.sbt-groll.conf" by default"""
      )

    val historyRef: SettingKey[String] =
      SettingKey[String](
        prefixed("historyRef"),
        """The ref (commit id, branch or tag) used for the Git history; "master" by default"""
      )

    val workingBranch: SettingKey[String] =
      SettingKey[String](
        prefixed("workingBranch"),
        """The working branch used by sbt-groll; "groll" by default"""
      )

    private def prefixed(key: String): String =
      s"groll${key.capitalize}"
  }

  override def projectSettings: Seq[Setting[_]] =
    List(
      Keys.commands += grollCommand,
      autoImport.configFile := new File(System.getProperty("user.home"), ".sbt-groll.conf"),
      autoImport.historyRef := "master",
      autoImport.workingBranch := "groll"
    )

  override def trigger: PluginTrigger =
    allRequirements

  private def grollCommand: Command =
    Command("groll")(parser)(Groll.apply)

  private def parser(state: State): Parser[GrollArg] = {
    import GrollArg._
    import sbt.complete.DefaultParsers._
    def arg(koanArg: GrollArg): Parser[GrollArg] =
      (Space ~> koanArg.toString.decapitalize).map(_ => koanArg)
    def stringOpt[A <: GrollArg: ClassTag](ctor: String => A): Parser[A] = {
      val name = classTag[A].runtimeClass.getSimpleName
      (Space ~> name.decapitalize ~> "=" ~> NotQuoted).map(ctor)
    }
    arg(Show) | arg(List) | arg(Next) | arg(Prev) | arg(Head) | arg(Initial) | stringOpt(Move) | stringOpt(Push) | arg(Version)
  }
}
