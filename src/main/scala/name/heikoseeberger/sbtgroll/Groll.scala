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
import sbt.{ Command, Keys, State, ThisProject }

private object Groll {

  import GrollOpts._

  val buildDefinition = """.+sbt|project/.+\.scala""".r

  def grollCommand = Command("groll")(parser)((state, args) => action(args)(state))

  def parser(state: State) = {
    import sbt.complete.DefaultParsers._
    opt(Show) | opt(List) | opt(Next) | opt(Prev) | opt(Head) | opt(Initial) | stringOpt(Move)
  }

  def action(args: Any)(implicit state: State) =
    try {
      implicit val git = Git(setting(Keys.baseDirectory))
      val grollRef = setting(GrollKeys.ref)
      val (currentId, currentMessage) = git.current
      val history = git.history(grollRef)
      args match {
        case Show =>
          withCurrentInHistory(currentId, history, grollRef) {
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
          withCurrentInHistory(currentId, history, grollRef)(
            groll(
              (history takeWhile { case (id, _) => id != currentId }).lastOption,
              "Already at the head of the commit history!",
              (id, message) => s">> $id $message"
            )
          )
        case Prev =>
          withCurrentInHistory(currentId, history, grollRef)(
            groll(
              (history dropWhile { case (id, _) => id != currentId }).tail.headOption,
              "Already at the first commit!",
              (id, message) => s"<< $id $message"
            )
          )
        case Head =>
          groll(
            if (currentId == history.head._1) None else Some(history.head),
            "Already at the head of the commit history!",
            (id, message) => s">> $id $message"
          )
        case Initial =>
          groll(
            history find { case (_, message) => message == "Initial state" },
            "There's no commit with message 'Initial state'!",
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

  def withCurrentInHistory(
    currentId: String,
    history: Seq[(String, String)],
    ref: String)(
      block: => State)(
        implicit state: State): State = {
    if (!(history map fst contains currentId)) {
      state.log.warn(s"Current commit '$currentId' is not part of the history of Groll ref '$ref'.")
      state
    } else
      block
  }

  def groll(idAndMessage: Option[(String, String)], warn: => String, info: (String, String) => String)(
    implicit git: Git, state: State): State =
    idAndMessage match {
      case None =>
        state.log.warn(warn)
        state
      case Some((id, message)) =>
        git.resetHard("master")
        git.clean()
        git.checkout(id, "groll")
        state.log.info(info(id, message))
        // if ()
        //   state.reload
        // else
        //   state
        state
    }
}
