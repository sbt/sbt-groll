/*
 * Copyright 2014 Heiko Seeberger
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
import org.eclipse.jgit.revwalk.RevCommit

package object sbtgroll {

  type Traversable[+A] = scala.collection.immutable.Traversable[A]

  type Iterable[+A] = scala.collection.immutable.Iterable[A]

  type Seq[+A] = scala.collection.immutable.Seq[A]

  type IndexedSeq[+A] = scala.collection.immutable.IndexedSeq[A]

  implicit class StringOps(val s: String) extends AnyVal {
    def decapitalize: String = {
      if (s == null)
        null
      else if (s.isEmpty)
        s
      else
        s.head.toLower +: s.tail
    }
  }

  implicit class RevCommitOps(val commit: RevCommit) extends AnyVal {
    def shortId: String =
      commit.abbreviate(7).name
  }

  implicit class FileOps(val file: File) extends AnyVal {
    def /(name: String): File =
      new File(file, name)
  }

  val tmpDir: File =
    new File(System.getProperty("java.io.tmpdir", "/tmp"))

  def fst[A, B](pair: (A, B)): A =
    pair._1
}
