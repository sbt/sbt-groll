/*
 * Copyright 2015 Heiko Seeberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.heikoseeberger.sbtgroll

import java.io.File
import java.nio.file.{ FileVisitResult, Files, Path, Paths, SimpleFileVisitor }
import java.nio.file.attribute.BasicFileAttributes
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.scalatest.{ Matchers, WordSpec }
import scala.sys.process._

class GitSpec extends WordSpec with Matchers {

  "Calling Git.checkout" should {
    "check out a new branch with the correct history" in {
      val f = fixture()
      import f._
      git.checkout("872b865", "groll")
      git.history("groll") shouldBe List(
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
      contents(dir).map(_.toString) shouldBe Set("1.txt", "2.txt", "4.txt")
    }
    "clean a dirty repo" in {
      val f = fixture("-dirty")
      import f._
      git.clean()
      contents(dir).map(_.toString) shouldBe Set("1.txt", "2.txt", "4.txt", "5.txt") // 5.txt has been staged, 6.txt is untracked
    }
  }

  "Calling Git.current" should {
    "return the correct commit" in {
      val f = fixture()
      import f._
      git.current() shouldBe "d26c92e" -> "Add 4.txt"
    }
  }

  "Calling Git.diff" should {
    "return the correct diff" in {
      val f = fixture()
      import f._
      git.diff("HEAD", "52e5f8e") shouldBe List("4.txt")
    }
  }

  "Calling Git.existsBranch" should {
    "return true for master and false for foo" in {
      val f = fixture()
      import f._
      git.existsRef("master") shouldBe true
      git.existsRef("none") shouldBe false
    }
  }

  "Calling Git.history" should {
    "return the correct history" in {
      val f = fixture()
      import f._
      git.history() shouldBe List(
        "d26c92e" -> "Add 4.txt",
        "52e5f8e" -> "Change 1.txt",
        "872b865" -> "Add 2.txt",
        "f695224" -> "Add 1.txt"
      )
    }
  }

  "Calling Git.findCommitIdWithTag" should {
    "return the objectId of the commit to which tag points to" in {
      val f = fixture("-with-tags")
      import f._
      git.findCommitIdWithTag("groll-initial").map(_.shortId) shouldBe Some("f695224")
    }
  }

  "Calling Git.resetHard" should {
    "work for a clean repo" in {
      val f = fixture()
      import f._
      git.resetHard()
      git.history() shouldBe List(
        "d26c92e" -> "Add 4.txt",
        "52e5f8e" -> "Change 1.txt",
        "872b865" -> "Add 2.txt",
        "f695224" -> "Add 1.txt"
      )
    }
    "work for a dirty repo" in {
      val f = fixture("-dirty")
      import f._
      git.resetHard()
      git.history() shouldBe List(
        "d26c92e" -> "Add 4.txt",
        "52e5f8e" -> "Change 1.txt",
        "872b865" -> "Add 2.txt",
        "f695224" -> "Add 1.txt"
      )
    }
  }

  def fixture(qualifier: String = "") =
    new {
      s"unzip -qo -d $tmpDir src/test/test-repo$qualifier.zip".!
      val dir        = tmpDir / s"test-repo$qualifier"
      val repository = (new FileRepositoryBuilder).setWorkTree(dir).build()
      val git        = new Git(repository)
    }

  def contents(dir: File): Set[Path] = {
    var paths = Set.empty[Path]
    val path  = Paths.get(dir.toURI)
    Files.walkFileTree(
      path,
      new SimpleFileVisitor[Path] {
        override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult =
          if (dir.getFileName.toString == ".git")
            FileVisitResult.SKIP_SUBTREE
          else
            FileVisitResult.CONTINUE
        override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
          paths += path relativize file
          FileVisitResult.CONTINUE
        }
      }
    )
    paths
  }
}
