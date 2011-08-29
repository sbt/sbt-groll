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

package name.heikoseeberger

import java.io.File
import scala.sys.process.{ Process, ProcessLogger }

package object groll {

  val newLine = System.getProperty("line.separator")

  /**
   * Executes a command in a process using the process API from the standard library.
   * @param command The command to be executed in a process. Must not be null!
   * @param workingDir The optional working directory for the process. Defaults to None. Must not be null!
   * @return The output of the process.
   */
  def execute(command: String, workingDir: File = new File(".")): Seq[String] = {
    require(command != null, "command must not be null!")
    require(workingDir != null, "workingDir must not be null!")
    var (out, err) = (List[String](), List[String]())
    val exitCode = Process(command, workingDir) ! ProcessLogger(s => out = s +: out, s => err = s +: err)
    if (exitCode == 0) out.reverse else throw new ProcessException(err.reverse mkString newLine)
  }

  private[groll] class ProcessException(message: String) extends Exception(message)
}
