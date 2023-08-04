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

import gooey.Var

/** Create a Component that takes it's value from a Var. */
final case class Observed[A](
    source: Var[A]
) extends Component[Observed.Algebra, A] {

  private[gooey] def build(algebra: Observed.Algebra)(
      env: algebra.Env
  ): algebra.UI[A] =
    algebra.observed(source)(env)
}
object Observed {
  trait Algebra extends gooey.Algebra {
    def observed[A](source: Var[A])(env: Env): UI[A]
  }
}
