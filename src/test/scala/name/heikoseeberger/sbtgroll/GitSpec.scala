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

import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.scalatest.{ Matchers, WordSpec }
import scala.sys.process._

class GitSpec extends WordSpec with Matchers {

  "Calling Git.history" should {
    "return the correct history" in {
      val f = fixture("history")
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
    "return the correct history" in {
      val f = fixture("resetHard")
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

  def fixture(qualifier: String) =
    new {
      s"unzip -qo -d $tmpDir src/test/test-repo-$qualifier.zip".!
      val repository = (new FileRepositoryBuilder).setWorkTree(tmpDir / s"test-repo-$qualifier").build()
      val git = new Git(repository)
    }
}
