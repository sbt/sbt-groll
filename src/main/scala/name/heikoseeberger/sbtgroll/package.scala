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
}
