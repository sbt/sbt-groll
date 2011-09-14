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

object GrollPlugin extends Plugin {
  override def settings = Seq(Keys.commands += Groll.grollCommand)
}

object Groll {

  val Show = "show"

  val Move = "move"

  val Prev = "prev"

  val Next = "next"

  val Head = "head"

  def parser(state: State) = {
    import sbt.complete.DefaultParsers._
    Space ~> Show | (Space ~> Move) ~ (Space ~> charClass(_ => true).+) | Space ~> Prev | Space ~> Next | Space ~> Head
  }

  def grollCommand = Command("groll")(parser) { (state, args) =>
    try {
      val history = execute("git log --oneline master") map { commit =>
        val (id, message) = commit splitAt 7
        id -> message.tail
      }
      logger(state).debug("History: %s" format (history mkString ", "))
      val current = execute("git log -n 1 --pretty=format:%h").head
      logger(state).debug("Current: %s" format current)
      assert(history map { _._1 } contains current, "Commit history must contain current commit!")
      args match {
        case (Move, chars: Seq[_]) =>
          val id = chars.mkString
          val commit = if (current == id) None else Some(id -> "")
          groll("Already at commit: %s" format id, "Moved to commit: %s %s", state)(commit)
        case Prev =>
          val commit = (history dropWhile { case (id, _) => id != current }).tail.headOption
          groll("Already arrived at the first commit!", "Moved back to previous commit: %s %s", state)(commit)
        case Next =>
          val commit = (history takeWhile { case (id, _) => id != current }).lastOption
          groll("Already arrived at the head of the commit history!", "Moved forward to next commit: %s %s", state)(commit)
        case Head =>
          val commit = if (current == history.head._1) None else Some(history.head)
          groll("Already arrived at the head of the commit history!", "Moved forward to the head of the commit history: %s %s", state)(commit)
      }
    } catch {
      case e: Exception =>
        logger(state).error(e.getMessage)
        state.fail
    }
  }

  private def resetCleanCheckout(commit: String): Unit =
    execute(Process("git reset --hard") #&& "git clean -df" #&& ("git checkout %s" format commit))

  private def groll(warn: String, info: String, state: State): Option[(String, String)] => State = {
    case None =>
      logger(state).warn(warn)
      state
    case Some((id, message)) =>
      resetCleanCheckout(id)
      logger(state).info(info.format(id, message))
      state.reload
  }
}
