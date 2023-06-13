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

/** Create a component that combines two child components where the first is
  * laid out above the second.
  */
final case class Above[Alg1 <: gooey.Algebra, Alg2 <: gooey.Algebra, A, B](
    top: Component[Alg1, A],
    bottom: Component[Alg2, B]
) extends Component[Alg1 & Alg2 & Above.Algebra, (A, B)] {
  def create(using algebra: Alg1 & Alg2 & Above.Algebra): algebra.UI[(A, B)] =
    algebra.above(top.create, bottom.create)
}
object Above {
  trait Algebra extends gooey.Algebra {
    def above[A, B](t: UI[A], b: UI[B]): UI[(A, B)]
  }
}
