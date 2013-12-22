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

package name.heikoseeberger

import java.io.File
import java.nio.file.{ Path, Paths }
import org.eclipse.jgit.revwalk.RevCommit
import sbt.{ BuildStructure, Extracted, Project, SettingKey, State, ThisProject }
import sbt.complete.Parser

package object sbtgroll {

  type Traversable[+A] = scala.collection.immutable.Traversable[A]

  type Iterable[+A] = scala.collection.immutable.Iterable[A]

  type Seq[+A] = scala.collection.immutable.Seq[A]

  type IndexedSeq[+A] = scala.collection.immutable.IndexedSeq[A]

  implicit class RevCommitOps(commit: RevCommit) {
    def shortId: String =
      (commit abbreviate 7).name
  }

  implicit class PathOps(path: Path) {
    def /(name: String): Path =
      path resolve name
  }

  implicit def pathToFile(path: Path): File =
    path.toFile

  val tmpDir: Path =
    Paths get System.getProperty("java.io.tmpdir", "/tmp")

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

  def setting[A](key: SettingKey[A])(implicit state: State) =
    key in ThisProject get structure(state).data getOrElse sys.error(s"$key undefined!")

  def structure(implicit state: State): BuildStructure =
    extracted.structure

  def extracted(implicit state: State): Extracted =
    Project extract state
}
