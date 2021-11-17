/*
 * Copyright 2016 Heiko Seeberger
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

private sealed trait GrollArg

private object GrollArg {

  final case object Show extends GrollArg

  final case object List extends GrollArg

  final case object Next extends GrollArg

  final case object Prev extends GrollArg

  final case object Head extends GrollArg

  final case object Initial extends GrollArg

  final case class Move(id: String) extends GrollArg

  final case class Push(branch: String) extends GrollArg

  final case object Help extends GrollArg
}
