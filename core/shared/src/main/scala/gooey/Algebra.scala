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

/** An Algebra defines an interface for some functionality that goes into
  * building a UI.
  */
trait Algebra {

  /** The type of any global shared state that this algebra needs to work with
    * to produce a UI.
    */
  type Env

  /** The type of the UI that an implementation of this algebra produces. */
  type UI[A]

  /** Produce any global shared state that this algebra needs to work with to
    * produce a UI.
    */
  def initialize(): Env
}
