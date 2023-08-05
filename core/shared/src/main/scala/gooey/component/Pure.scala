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

/** Component that produces a value but doesn't produce an visual output. */
final case class Pure[A](value: A) extends Component[Pure.Algebra, A] {
  private[gooey] def build(algebra: Pure.Algebra)(
      env: algebra.Env
  ): algebra.UI[A] =
    algebra.pure(value)(env)
}
object Pure {
  trait Algebra extends gooey.Algebra {
    def pure[A](value: A)(env: Env): UI[A]
  }
}
