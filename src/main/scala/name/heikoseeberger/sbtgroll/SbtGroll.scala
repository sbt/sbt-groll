/*
 * Copyright 2014 Heiko Seeberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package name.heikoseeberger.sbtgroll

import sbt.{ Command, Keys, Plugin, Setting, SettingKey, State }
import sbt.complete.Parser
import scala.reflect.{ ClassTag, classTag }

object SbtGroll extends Plugin {

  object GrollKey {

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

    private def prefixed(key: String) = "groll" + key.capitalize
  }

  override def settings: Seq[Setting[_]] =
    List(
      Keys.commands += grollCommand,
      GrollKey.historyRef := "master",
      GrollKey.workingBranch := "groll"
    )

  private def grollCommand =
    Command("groll")(parser)(Groll.apply)

  private def parser(state: State) = {
    import GrollArg._
    import sbt.complete.DefaultParsers._
    def arg(koanArg: GrollArg): Parser[GrollArg] = {
      (Space ~> koanArg.toString.toLowerCase) map (_ => koanArg)
    }
    def stringOpt[A <: GrollArg: ClassTag](ctor: String => A): Parser[A] = {
      import sbt.complete.DefaultParsers._
      val name = classTag[A].runtimeClass.getName
      (Space ~> name.toLowerCase ~> "=" ~> NotQuoted) map ctor
    }
    arg(Show) | arg(List) | arg(Next) | arg(Prev) | arg(Head) | arg(Initial) | stringOpt(Move)
  }
}
