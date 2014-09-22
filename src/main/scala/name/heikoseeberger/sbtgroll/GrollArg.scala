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

private sealed trait GrollArg

private object GrollArg {

  case object Show extends GrollArg

  case object List extends GrollArg

  case object Next extends GrollArg

  case object Prev extends GrollArg

  case object Head extends GrollArg

  case object Initial extends GrollArg

  case class Move(id: String) extends GrollArg

  case object PushSolutions extends GrollArg

  case object Version extends GrollArg
}
