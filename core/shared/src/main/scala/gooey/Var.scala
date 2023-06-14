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

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable

/** A `Var` represents a time varying value. At all points in time a `Var`
  * contains a value of type `A`. Interested parties can subscribe to receive
  * updates when that value changes, and may react accordingly
  *
  * Note that `Vars` introduce state into a Gooey program, and this can break
  * substitution. (Describe more here.)
  *
  * Note that `Vars` are translated to a backend specific implementation. `Vars`
  * have no actual behavior themselves; they don't do anything. They only record
  * the behavior that should happen in the backend equivalent.
  *
  * Gooey makes no particular guarantees about how updates to `Vars` are
  * propagated. This is because `Vars` represent an abstraction over an backend
  * specific implementation, and different backends may have different
  * semantics.
  */
sealed trait Var[A] {
  import Var.*

  def map[B](f: A => B): Var[B] = {
    val view = View[A, B](this, f)
    view
  }
}
object Var {
  private[gooey] final case class View[A, B](source: Var[A], f: A => B)
      extends Var[B]
  private[gooey] final case class Constant[A](value: A) extends Var[A] {
    import Var.Id

    final val id: Id = Id.next()
  }

  opaque type Id = Int
  object Id {
    private[this] val nextId = new AtomicInteger()
    def next(): Id = nextId.getAndIncrement()
  }

  /** Create a Var that always holds the same value. */
  def constant[A](value: A): Var[A] =
    Constant(value)

  /** Create a `Var` whose value comes from somewhere else; usually from a
    * `Component`.
    */
  def writable[A](default: A): WritableVar[A] =
    WritableVar[A](default)
}

/** A `Var` that allows the value contained within it to be changed. */
final case class WritableVar[A](default: A) extends Var[A] {
  import Var.Id

  final val id: Id = Id.next()
}
