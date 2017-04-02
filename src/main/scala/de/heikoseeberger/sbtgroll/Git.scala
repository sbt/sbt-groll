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
import org.eclipse.jgit.api.ResetCommand.ResetType
import org.eclipse.jgit.api.{ Git => JGit }
import org.eclipse.jgit.lib.{ ObjectId, Repository }
import org.eclipse.jgit.revwalk.{ RevCommit, RevWalk }
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.{
  CredentialsProvider,
  RefSpec,
  UsernamePasswordCredentialsProvider
}
import org.eclipse.jgit.treewalk.{ AbstractTreeIterator, CanonicalTreeParser }
import scala.collection.JavaConversions._

object Git {
  def apply(workTree: File): Git =
    new Git((new FileRepositoryBuilder).setWorkTree(workTree).build())
}

class Git(repository: Repository) {

  private val jgit = new JGit(repository)

  /**
    * @param tagname Name of a tag (not containing 'refs/tags/').
    * @return Option with the commit id to which given tag points to.
    */
  def findCommitIdWithTag(tagname: String): Option[ObjectId] =
    jgit
      .tagList()
      .call()
      .find(_.getName == s"refs/tags/$tagname")
      .map(ref => {
        // Must peel the ref to get the peeledObjectId which points
        // to the commit to which the tags belongs
        jgit.getRepository.peel(ref).getPeeledObjectId
      })

  def checkout(ref: String, branch: String): Unit = {
    jgit.checkout
      .setName("master")
      .call()
    jgit.branchDelete
      .setBranchNames(branch)
      .setForce(true)
      .call()
    jgit.checkout
      .setName(branch)
      .setStartPoint(ref)
      .setCreateBranch(true)
      .call()
  }

  def clean(): Unit =
    jgit.clean
      .setCleanDirectories(true)
      .setIgnore(true)
      .call()

  def current(): (String, String) =
    jgit.log
      .setMaxCount(1)
      .call()
      .map(idAndMessage)
      .head

  def diff(newRef: String, oldRef: String): Seq[String] =
    jgit.diff
      .setNewTree(tree(newRef))
      .setOldTree(tree(oldRef))
      .call()
      .toList
      .map(_.getNewPath)

  def existsRef(ref: String): Boolean =
    repository.resolve(ref) != null

  def history(ref: String = "master"): Seq[(String, String)] =
    jgit.log
      .add(repository.resolve(ref))
      .call()
      .toList
      .map(idAndMessage)

  def pushHead(source: String, destination: String, username: String, password: String): Unit =
    jgit.push
      .setRemote("origin-https")
      .setRefSpecs(new RefSpec(s"$source:$destination"))
      .setForce(true)
      .setCredentialsProvider(credentialsProvider(username, password))
      .call()

  def resetHard(): Unit =
    jgit.reset
      .setMode(ResetType.HARD)
      .call()

  private def idAndMessage(commit: RevCommit): (String, String) =
    commit.shortId -> commit.getShortMessage

  private def tree(ref: String): AbstractTreeIterator = {
    val tree = {
      val walk   = new RevWalk(repository)
      val commit = walk.parseCommit(repository.resolve(ref))
      walk.parseTree(commit.getTree.getId)
    }
    val parser = new CanonicalTreeParser
    val reader = repository.newObjectReader
    try {
      parser.reset(reader, tree.getId)
      parser
    } finally reader.close()
  }

  private def credentialsProvider(username: String, password: String): CredentialsProvider =
    new UsernamePasswordCredentialsProvider(username, password)
}
