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

package gooey.component

import gooey.WritableVar

/** Add an observer to the source component, which pushes the value produced by
  * the source to the given WritableVar.
  */
final case class Observe[Alg <: gooey.Algebra, A](
    source: Component[Alg, A],
    observer: WritableVar[A]
) extends Component[Alg & Observe.Algebra, A] {

  private[gooey] def build(algebra: Alg & Observe.Algebra)(
      env: algebra.Env
  ): algebra.UI[A] =
    algebra.observe(source.build(algebra)(env), observer)(env)
}
object Observe {
  trait Algebra extends gooey.Algebra {
    def observe[A](source: UI[A], observer: WritableVar[A])(env: Env): UI[A]
  }
}
