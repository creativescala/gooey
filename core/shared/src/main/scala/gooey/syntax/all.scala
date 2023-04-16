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

package gooey.syntax

import gooey.Algebra
import gooey.component.*

object all {
  extension [Alg1 <: Algebra, A](first: Component[Alg1, A]) {
    def and[Alg2 <: Algebra, B](
        second: Component[Alg2, B]
    ): Component[Alg1 & Alg2 & And.Algebra, (A, B)] =
      And(first, second)
  }

  extension [Alg <: Algebra, A](c: Component[Alg, A]) {
    def map[B](f: A => B): Component[Alg & Map.Algebra, B] = Map(c, f)
  }
}
