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

import SbtGroll.GrollKeys
import sbt.{ Command, Keys, State, ThisProject }
import scala.collection.immutable.Seq
import scala.sys.process.Process

private object Groll {

  import GrollOpts._

  val BuildDefinition = """project/.*\.scala""".r

  val cmd = if (isWindowsShell) "cmd /c git" else "git"

  def grollCommand = Command("groll")(parser)((state, args) => action(args)(state))

  def parser(state: State) = {
    import sbt.complete.DefaultParsers._
    opt(Show) | opt(List) | opt(Next) | opt(Prev) | opt(Head) | opt(Initial) | stringOpt(Move)
  }

  def action(args: Any)(implicit state: State) =
    try {
      val revision = GrollKeys.revision in ThisProject get structure.data getOrElse "master"
      val postCommands = GrollKeys.postCommands in ThisProject get structure.data getOrElse Nil
      val current = execute("%s log -n 1 --pretty=format:%%h" format cmd).head
      val history = execute("%s log --oneline %s".format(cmd, revision)) map idAndMessage
      args match {
        case Show =>
          withCurrentInHistory(current, history, revision) {
            state.log.info("Current commit: %s %s".format(current, history.toMap.apply(current)))
            state
          }
        case List =>
          history foreach {
            case (id, msg) =>
              if (id == current)
                state.log.info(">>>>>>> %s %s <<<<<<<".format(id, msg))
              else
                state.log.info("%s %s".format(id, msg))
          }
          state
        case Next =>
          withCurrentInHistory(current, history, revision)(
            groll(
              (history takeWhile { case (id, _) => id != current }).lastOption,
              "Already arrived at the head of the commit history!",
              "Moved forward to next commit: %s %s",
              postCommands
            )
          )
        case Prev =>
          withCurrentInHistory(current, history, revision)(
            groll(
              (history dropWhile { case (id, _) => id != current }).tail.headOption,
              "Already arrived at the first commit!",
              "Moved back to previous commit: %s %s",
              postCommands
            )
          )
        case Head =>
          groll(
            if (current == history.head._1) None else Some(history.head),
            "Already arrived at the head of the commit history!",
            "Moved forward to the head of the commit history: %s %s",
            postCommands
          )
        case Initial =>
          groll(
            history find { case (_, message) => message == "Initial state" },
            "Already arrived at the head of the commit history!",
            "Moved forward to the head of the commit history: %s %s",
            postCommands
          )
        case (Move, id: String) =>
          groll(
            if (current == id) None else Some(id -> ""),
            "Already at commit: %s" format id,
            "Moved to commit: %s %s",
            postCommands
          )
      }
    } catch {
      case e: Exception =>
        state.log.error(e.getMessage)
        state.fail
    }

  def idAndMessage(commit: String) = {
    val (id, message) = commit splitAt 7
    id -> message.tail
  }

  def withCurrentInHistory(
    current: String,
    history: Seq[(String, String)],
    revision: String)(
      block: => State)(
        implicit state: State) = {
    if (!(history map fst contains current)) {
      state.log.warn("Current commit '%s' is not part of the history of revision '%s'.".format(current, revision))
      state
    } else
      block
  }

  def groll(
    idAndMessage: Option[(String, String)],
    warn: String,
    info: String,
    postCommands: Seq[String])(
      implicit state: State) =
    idAndMessage match {
      case None =>
        state.log.warn(warn)
        state
      case Some((id, message)) =>
        val output = resetCleanCheckout(id)
        state.log.info(info.format(id, message))
        if (output exists isBuildDefinition)
          (postCommands ::: state).reload
        else
          postCommands ::: state
    }

  def resetCleanCheckout(id: String)(implicit state: State) =
    execute(
      Process(("%s reset --hard") format cmd) #&&
        ("%s clean -df" format cmd) #&&
        ("%s diff --name-only %s" format (cmd, id)) #&&
        ("%s checkout %s" format (cmd, id)))

  def isBuildDefinition(s: String) = (s endsWith ".sbt") || BuildDefinition.findFirstIn(s).isDefined

  def isWindowsShell = {
    val isCygwin = {
      val ostype = System.getenv("OSTYPE")
      ostype != null && ostype.toLowerCase.contains("cygwin")
    }
    val isWindows = System.getProperty("os.name", "").toLowerCase.contains("windows")
    isWindows && !isCygwin
  }
}
