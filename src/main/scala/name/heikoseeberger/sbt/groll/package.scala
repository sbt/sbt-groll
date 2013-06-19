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

package name.heikoseeberger.sbt

import sbt.{ BuildStructure, Extracted, Project, State }
import sbt.complete.Parser
import scala.collection.immutable.Seq
import scala.sys.process.{ ProcessBuilder, ProcessLogger }
import scala.util.Properties.{ lineSeparator => newLine }

package object groll {

  def fst[A, B](pair: (A, B)): A =
    pair._1

  def opt(key: String): Parser[String] = {
    import sbt.complete.DefaultParsers._
    (Space ~> key)
  }

  def stringOpt(key: String): Parser[(String, String)] = {
    import sbt.complete.DefaultParsers._
    (Space ~> key ~ ("=" ~> charClass(_ => true).+)) map { case (k, v) => k -> v.mkString }
  }

  def execute(process: ProcessBuilder)(implicit state: State): Seq[String] = {
    state.log.debug("About to execute process '%s'." format process)
    var (out, err) = (Vector[String](), Vector[String]())
    val exitCode = process ! ProcessLogger(out :+= _, err :+= _)
    if (exitCode == 0)
      out
    else
      sys.error("Exit code: %s%s%s".format(exitCode, newLine, err mkString newLine))
  }

  def extracted(implicit state: State): Extracted =
    Project extract state

  def structure(implicit state: State): BuildStructure =
    extracted.structure
}
