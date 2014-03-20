/*
 * Copyright 2013 Heiko Seeberger
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

import SbtGroll.GrollKeys
import sbt.{ Command, Keys, State }

private object Groll {

  import GrollOpts._

  object Action {
    def apply(args: Any, state: State): Action =
      new Action(args, state)
  }

  class Action(args: Any, state: State) {

    val git = Git(setting(Keys.baseDirectory, state))

    val historyRef = setting(GrollKeys.historyRef, "master", state)

    val workingBranch = setting(GrollKeys.workingBranch, "groll", state)

    val (currentId, currentMessage) = git.current()

    val history = git.history(historyRef)

    def apply(): State =
      try {
        args match {
          case Show =>
            withCurrentInHistory {
              state.log.info(s"== $currentId $currentMessage")
              state
            }
          case List =>
            for ((id, message) <- history) {
              if (id == currentId)
                state.log.info(s"== $id $message")
              else
                state.log.info(s"   $id $message")
            }
            state
          case Next =>
            withCurrentInHistory {
              groll(
                (history takeWhile { case (id, _) => id != currentId }).lastOption,
                "Already at the head of the commit history!",
                (id, message) => s">> $id $message"
              )
            }
          case Prev =>
            withCurrentInHistory {
              groll(
                (history dropWhile { case (id, _) => id != currentId }).tail.headOption,
                "Already at the first commit!",
                (id, message) => s"<< $id $message"
              )
            }
          case Head =>
            groll(
              if (currentId == history.head._1) None else Some(history.head),
              "Already at the head of the commit history!",
              (id, message) => s">> $id $message"
            )
          case Initial =>
            groll(
              history find { case (_, message) => message startsWith "Initial state" },
              "There's no commit with a message starting with 'Initial state'!",
              (id, message) => s"<< $id $message"
            )
          case (Move, id: String) =>
            groll(
              if (currentId == id) None else Some(id -> history.toMap.getOrElse(id, "")),
              s"Already at $id",
              (id, message) => s"<> $id $message"
            )
        }
      } catch {
        case e: Exception =>
          state.log.error(e.getMessage)
          state.fail
      }

    def withCurrentInHistory(block: => State): State = {
      if (!(history map fst contains currentId)) {
        state.log.warn(s"Current commit '$currentId' is not part of the history defined by '$historyRef'.")
        state
      } else
        block
    }

    def groll(idAndMessage: Option[(String, String)], warn: => String, info: (String, String) => String): State =
      idAndMessage match {
        case None =>
          state.log.warn(warn)
          state
        case Some((id, message)) =>
          git.resetHard()
          git.clean()
          git.checkout(id, workingBranch)
          state.log.info(info(id, message))
          if (git.diff(id, currentId) exists (s => buildDefinition.pattern.matcher(s).matches))
            state.reload
          else
            state
      }
  }

  val buildDefinition = """.+sbt|project/.+\.scala""".r

  def grollCommand = Command("groll")(parser)((state, args) => Action(args, state)())

  def parser(state: State) = {
    import sbt.complete.DefaultParsers._
    opt(Show) | opt(List) | opt(Next) | opt(Prev) | opt(Head) | opt(Initial) | stringOpt(Move)
  }
}
