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

import java.nio.file.{ FileVisitResult, Files, Path, SimpleFileVisitor }
import java.nio.file.attribute.BasicFileAttributes
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.scalatest.{ Matchers, WordSpec }
import scala.sys.process._

class GitSpec extends WordSpec with Matchers {

  "Calling Git.checkout" should {
    "check out a new branch with the correct history" in {
      val f = fixture()
      import f._
      git.checkout("872b865", "current")
      git.history("current") shouldEqual
        List(
          "872b865" -> "Add 2.txt",
          "f695224" -> "Add 1.txt"
        )
    }
  }

  "Calling Git.clean" should {
    "leave a clean repo unchanged" in {
      val f = fixture()
      import f._
      git.clean()
      contents(path) map (_.toString) shouldEqual Set("1.txt", "2.txt", "4.txt")
    }
    "clean a dirty repo" in {
      val f = fixture("-dirty")
      import f._
      git.clean()
      contents(path) map (_.toString) shouldEqual Set("1.txt", "2.txt", "4.txt", "5.txt") // 5.txt has been staged, 6.txt is untracked
    }
  }

  "Calling Git.current" should {
    "return the correct commit" in {
      val f = fixture()
      import f._
      git.current() shouldEqual "d26c92e" -> "Add 4.txt"
    }
  }

  "Calling Git.history" should {
    "return the correct history" in {
      val f = fixture()
      import f._
      git.history() shouldEqual
        List(
          "d26c92e" -> "Add 4.txt",
          "52e5f8e" -> "Change 1.txt",
          "872b865" -> "Add 2.txt",
          "f695224" -> "Add 1.txt"
        )
    }
  }

  "Calling Git.resetHard" should {
    "work for a clean repo" in {
      val f = fixture()
      import f._
      git.resetHard("872b865")
      git.history() shouldEqual
        List(
          "872b865" -> "Add 2.txt",
          "f695224" -> "Add 1.txt"
        )
    }
    "work for a dirty repo" in {
      val f = fixture("-dirty")
      import f._
      git.resetHard("52e5f8e")
      git.history() shouldEqual
        List(
          "52e5f8e" -> "Change 1.txt",
          "872b865" -> "Add 2.txt",
          "f695224" -> "Add 1.txt"
        )
    }
  }

  def fixture(qualifier: String = "") =
    new {
      s"unzip -qo -d $tmpDir src/test/test-repo$qualifier.zip".!
      val path = tmpDir / s"test-repo$qualifier"
      val repository = (new FileRepositoryBuilder).setWorkTree(path).build()
      val git = new Git(repository)
    }

  def contents(path: Path): Set[Path] = {
    var paths = Set.empty[Path]
    Files.walkFileTree(path, new SimpleFileVisitor[Path] {
      override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult =
        if (dir.getFileName.toString == ".git")
          FileVisitResult.SKIP_SUBTREE
        else
          FileVisitResult.CONTINUE
      override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        paths += path relativize file
        FileVisitResult.CONTINUE
      }
    })
    paths
  }
}
