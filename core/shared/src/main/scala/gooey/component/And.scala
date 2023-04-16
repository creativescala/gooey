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

/** Create a component that combines two child components without specifying how
  * the child components are laid out relative to one another. The backend can
  * decide whether to lay them out one above the other or one next to another.
  */
final case class And[Alg1 <: gooey.Algebra, Alg2 <: gooey.Algebra, A, B](
    first: Component[Alg1, A],
    second: Component[Alg2, B]
) extends Component[Alg1 & Alg2 & And.Algebra, (A, B)] {
  def create(using algebra: Alg1 & Alg2 & And.Algebra): algebra.UI[(A, B)] =
    algebra.and(first.create, second.create)
}
object And {
  trait Algebra extends gooey.Algebra {
    def and[A, B](f: UI[A], s: UI[B]): UI[(A, B)]
  }
}
