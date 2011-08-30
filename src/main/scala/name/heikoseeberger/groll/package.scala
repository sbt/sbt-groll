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
import scala.sys.process.{ ProcessBuilder, ProcessLogger }

package object groll {

  val newLine = System.getProperty("line.separator")

  def execute(process: ProcessBuilder): Seq[String] = {
    require(process != null, "process must not be null!")
    var (out, err) = (Vector[String](), Vector[String]())
    if (process ! ProcessLogger(out :+= _, err :+= _) == 0) out
    else throw new ExecutionException(err mkString newLine)
  }

  private class ExecutionException(message: String) extends RuntimeException(message)
}
