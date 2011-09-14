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

  val Move = "move"

  val Prev = "prev"

  val Next = "next"

  val Head = "head"

  def parser(state: State) = {
    import sbt.complete.DefaultParsers._
    (Space ~> Move) ~ (Space ~> charClass(_ => true).+) | Space ~> Prev | Space ~> Next | Space ~> Head
  }

  def grollCommand = Command("groll")(parser) { (state, args) =>
    try {
      val history = execute("git log --pretty=format:%h master")
      logger(state).debug("History: %s" format (history mkString ", "))
      val current = execute("git log -n 1 --pretty=format:%h").head
      logger(state).debug("Current: %s" format current)
      assert(history contains current, "Commit history must contain current commit!")

      args match {
        case (Move, chars: Seq[_]) => {
          val commit = chars.mkString
          if (current == commit) {
            logger(state).warn("Already at commit: %s" format commit)
            state
          } else {
            resetCleanCheckout(commit)
            logger(state).info("Moved to commit: %s" format commit)
            state.reload
          }
        }
        case Prev => (history dropWhile { _ != current }).tail.headOption match {
          case None =>
            logger(state).warn("Already arrived at the first commit!")
            state
          case Some(prevCommit) =>
            resetCleanCheckout(prevCommit)
            logger(state).info("Moved back to previous commit: %s" format prevCommit)
            state.reload
        }
        case Next => (history takeWhile { _ != current }).lastOption match {
          case None =>
            logger(state).warn("Already arrived at the head of the commit history!")
            state
          case Some(nextCommit) =>
            resetCleanCheckout(nextCommit)
            logger(state).info("Moved forward to next commit: %s" format nextCommit)
            state.reload
        }
        case Head => {
          val headCommit = history.head
          if (current == headCommit) {
            logger(state).warn("Already arrived at the head of the commit history!")
            state
          } else {
            resetCleanCheckout(headCommit)
            logger(state).info("Moved forward to the head of the commit history: %s" format headCommit)
            state.reload
          }
        }
      }
    } catch {
      case e: Exception =>
        logger(state).error(e.getMessage)
        state.fail
    }
  }

  private def resetCleanCheckout(commit: String): Unit =
    execute(Process("git reset --hard") #&& "git clean -df" #&& ("git checkout %s" format commit))
}
