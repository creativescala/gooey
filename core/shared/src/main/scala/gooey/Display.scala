/*
 * Copyright 2023 Creative Scala
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

package gooey

/** Enumeration for specifying if a Component is shown or hidden. The semantics
  * are the same as the 'display' style in CSS: a hidden component is not
  * visible and takes up no space on the screen.
  */
enum Display {
  case Show
  case Hide
}
object Display {

  /** Utility to convert a Boolean to a Display, under the assumption that true
    * means shown.
    */
  def fromBoolean(show: Boolean): Display =
    if show then Show else Hide
}
