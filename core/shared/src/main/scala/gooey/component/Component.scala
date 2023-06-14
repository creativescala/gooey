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

import cats.Applicative
import gooey.Algebra
import gooey.syntax.all.*

/** A UI component that produces a value of type `A` and requires the
  * capabilities defined in `Alg`.
  *
  * `Component` has an `Applicative` instance, which requires `Map`, `Pure`, and
  * `And` algebras.
  */
trait Component[-Alg <: Algebra, A] {

  /** Utility to change the algebra type associated with this component.
    * Sometimes useful to work around type inference issues.
    */
  def as[AA <: Alg]: Component[AA, A] = this

  /** Given implementations of the algebras required by this `Component`,
    * produce a backend specific user interface representation.
    */
  final def create(using algebra: Alg): algebra.UI[A] =
    build(algebra)(algebra.initialize())

  /** Build the UI for the specific subtype given the algebra and an environment
    */
  private[gooey] def build(algebra: Alg)(env: algebra.Env): algebra.UI[A]
}
object Component {
  given componentApplicative[Alg <: And.Algebra & Map.Algebra & Pure.Algebra]
      : Applicative[Component[Alg, *]] with {

    def pure[A](x: A): Component[Alg, A] = Pure(x)

    def ap[A, B](ff: Component[Alg, A => B])(
        fa: Component[Alg, A]
    ): Component[Alg, B] =
      ff.and(fa).map((f, a) => f(a))
  }
}
