/*
 * Copyright 2011 Heiko Seeberger
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

package name.heikoseeberger.groll

import sbt.{ Command, Keys, Plugin, State }
import sbt.CommandSupport.logger
import scala.sys.process.Process

private object Groll {

  val cmd = if (isWindowsShell) "cmd /c git" else "git"

  def grollCommand = Command("groll")(parser)((state, args) => action(args)(state))

  def parser(state: State) = {
    import GrollOpts._
    import sbt.complete.DefaultParsers._
    opt(Show) | opt(List) | opt(Next) | opt(Prev) | opt(Head) | stringOpt(Move)
  }

  def action(args: Any)(implicit state: State) = {
    import GrollOpts._
    try {
      val history = execute(cmd + " log --oneline master") map idAndMessage
      logger(state).debug("History: %s" format (history mkString ", "))
      val current = execute(cmd + " log -n 1 --pretty=format:%h").head
      logger(state).debug("Current: %s" format current)
      assert(history map fst contains current, "Commit history must contain current commit!")
      args match {
        case Show =>
          logger(state).info("Current commit: %s %s".format(current, (history.toMap).apply(current)))
          state
        case List =>
          history foreach { case (id, msg) => logger(state).info("%s %s".format(id, msg)) }
          state
        case Next =>
          val commit = (history takeWhile { case (id, _) => id != current }).lastOption
          groll("Already arrived at the head of the commit history!", "Moved forward to next commit: %s %s", state)(commit)
        case Prev =>
          val commit = (history dropWhile { case (id, _) => id != current }).tail.headOption
          groll("Already arrived at the first commit!", "Moved back to previous commit: %s %s", state)(commit)
        case Head =>
          val commit = if (current == history.head._1) None else Some(history.head)
          groll("Already arrived at the head of the commit history!", "Moved forward to the head of the commit history: %s %s", state)(commit)
        case (Move, id: String) =>
          val commit = if (current == id) None else Some(id -> "")
          groll("Already at commit: %s" format id, "Moved to commit: %s %s", state)(commit)
      }
    } catch {
      case e: Exception =>
        logger(state).error(e.getMessage)
        state.fail
    }
  }

  def idAndMessage(commit: String) = {
    val (id, message) = commit splitAt 7
    id -> message.tail
  }

  def groll(warn: String, info: String, state: State): Option[(String, String)] => State = {
    case None =>
      logger(state).warn(warn)
      state
    case Some((id, message)) =>
      val output = resetCleanCheckout(id)
      logger(state).info(info.format(id, message))
      if (output exists isBuildDefinition)
        state.reload
      else
        state
  }

  def resetCleanCheckout(commit: String) =
    execute(
      Process(("%s reset --hard") format cmd) #&&
        ("%s clean -df" format cmd) #&&
        ("%s diff --name-only %s" format (cmd, commit)) #&&
        ("%s checkout %s" format (cmd, commit)))

  def isBuildDefinition(s: String) = (s endsWith "build.sbt") || (s endsWith "Build.scala")

  // TODO - Something less lame here.
  def isWindowsShell = {
    val ostype = System.getenv("OSTYPE")
    val isCygwin = ostype != null && ostype.toLowerCase.contains("cygwin")
    val isWindows = System.getProperty("os.name", "").toLowerCase.contains("windows")
    isWindows && !isCygwin
  }
}
