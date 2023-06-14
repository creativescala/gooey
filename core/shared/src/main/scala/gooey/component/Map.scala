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

import cats.data.Chain
import gooey.WritableVar

/** Component that produces a value but doesn't produce an visual output. */
final case class Map[Alg <: gooey.Algebra, A, B](
    source: Component[Alg, A],
    f: A => B
) extends Component[Alg & Map.Algebra, B] {
  private[gooey] def build(algebra: Alg & Map.Algebra)(
      env: algebra.Env
  ): algebra.UI[B] =
    algebra.map(source.build(algebra)(env), f)(env)
}
object Map {
  trait Algebra extends gooey.Algebra {
    def map[A, B](source: UI[A], f: A => B)(env: Env): UI[B]
  }
}
