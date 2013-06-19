/*
 * Copyright 2011-2013 Heiko Seeberger
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

package name.heikoseeberger.sbt.groll

import sbt.{ Keys, Plugin, Setting, SettingKey }
import scala.collection.immutable.Seq

object SbtGroll extends Plugin {

  override def settings: Seq[Setting[_]] =
    List(Keys.commands += Groll.grollCommand)

  object GrollKeys {

    val postCommands: SettingKey[Seq[String]] =
      SettingKey[Seq[String]](
        prefixed("postCommands"),
        """The commands to be executed after "rolling"."""
      )

    val revision: SettingKey[String] =
      SettingKey[String](
        prefixed("revision"),
        "The revision (branch or tag) used for the Git history."
      )

    private def prefixed(key: String) = "groll" + key.capitalize
  }
}
