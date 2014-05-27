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

package name.heikoseeberger.sbtgroll

import java.io.File
import org.eclipse.jgit.api.{ Git => JGit }
import org.eclipse.jgit.api.ResetCommand.ResetType
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.{ RevCommit, RevWalk }
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.{ CredentialsProvider, RefSpec, UsernamePasswordCredentialsProvider }
import org.eclipse.jgit.treewalk.{ AbstractTreeIterator, CanonicalTreeParser }
import scala.collection.JavaConversions._

object Git {
  def apply(workTree: File): Git =
    new Git((new FileRepositoryBuilder).setWorkTree(workTree).build())
}

class Git(repository: Repository) {

  private val jgit = new JGit(repository)

  def checkout(ref: String, branch: String): Unit = {
    (jgit.checkout setName "master").call()
    (jgit.branchDelete setBranchNames branch setForce true).call()
    (jgit.checkout setName branch setStartPoint ref setCreateBranch true).call()
  }

  def clean(): Unit = {
    val command = jgit.clean setCleanDirectories true setIgnore true
    command.call()
  }

  def current(): (String, String) = {
    val command = jgit.log setMaxCount 1
    command.call().toList map idAndMessage head
  }

  def diff(newRef: String, oldRef: String): Seq[String] = {
    val command = jgit.diff setNewTree tree(newRef) setOldTree tree(oldRef)
    command.call().toList map (_.getNewPath)
  }

  def history(ref: String = "master"): Seq[(String, String)] = {
    val command = jgit.log add repository.resolve(ref)
    command.call().toList map idAndMessage
  }

  def pushHead(source: String, destination: String, username: String, password: String): Unit = {
    val refSpec = new RefSpec(s"$source:$destination")
    val command =
      jgit.push() setRemote "origin-https" setRefSpecs refSpec setForce true setCredentialsProvider credentialsProvider(username, password)
    command.call()
  }

  def resetHard(): Unit = {
    val command = jgit.reset setMode ResetType.HARD
    command.call()
  }

  private def idAndMessage(commit: RevCommit): (String, String) =
    commit.shortId -> commit.getShortMessage

  private def tree(ref: String): AbstractTreeIterator = {
    val tree = {
      val walk = new RevWalk(repository)
      val commit = walk.parseCommit(repository.resolve(ref))
      walk.parseTree(commit.getTree.getId)
    }
    val parser = new CanonicalTreeParser
    val reader = repository.newObjectReader
    try {
      parser.reset(reader, tree.getId)
      parser
    } finally reader.release()
  }

  private def credentialsProvider(username: String, password: String): CredentialsProvider =
    new UsernamePasswordCredentialsProvider(username, password)
}
