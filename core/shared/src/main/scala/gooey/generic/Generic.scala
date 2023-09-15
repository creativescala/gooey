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

package gooey.generic

import gooey.Algebra
import gooey.component.*
import gooey.syntax.all.*

import scala.compiletime.*
import scala.deriving.*

type ToComponentAlgebra = And.Algebra & Map.Algebra & Pure.Algebra &
  Slider.Algebra & Text.Algebra & Textbox.Algebra & Algebra

trait ToComponent[A] {
  def toComponent: Component[ToComponentAlgebra, A]
}

object ToComponent {
  inline def derived[A](using m: Mirror.Of[A]): ToComponent[A] = {
    inline m match {
      case s: Mirror.SumOf[A]     => ???
      case p: Mirror.ProductOf[A] => fromProduct(p)
      case _ =>
        scala.compiletime.error(s"Cannot create ToComponent for mirror $m")
    }
  }

  def withLabel[A](
      label: String,
      component: Component[ToComponentAlgebra, A]
  ): Component[ToComponentAlgebra, A] =
    component match {
      case s: Slider  => s.withLabel(label)
      case t: Textbox => t.withLabel(label)
      case other      => other
    }

  inline def fromProduct[A](m: Mirror.ProductOf[A]): ToComponent[A] = {
    new ToComponent[A] {
      def toComponent: Component[ToComponentAlgebra, A] = {
        type ElemToComponent = Tuple.Map[m.MirroredElemTypes, ToComponent]
        val elemToComponents: ElemToComponent = summonAll[ElemToComponent]
        val elemNames = constValueTuple[m.MirroredElemLabels]
        val component: Component[ToComponentAlgebra, A] =
          elemToComponents.toList
            .zip(elemNames.toList)
            .asInstanceOf[List[(ToComponent[?], String)]]
            .foldLeft(Pure[Tuple](EmptyTuple).widen[ToComponentAlgebra]) {
              (accum, elt) =>
                val (toC, name) = elt
                val component = withLabel(name, toC.toComponent)

                accum.and(component).map((l, r) => l :* r)
            }
            .map(tuple => m.fromProduct(tuple))
        component
      }
    }
  }

  given intComponent: ToComponent[Int] with {
    def toComponent: Component[ToComponentAlgebra, Int] =
      Slider.default.widen[ToComponentAlgebra]
  }

  given stringComponent: ToComponent[String] with {
    def toComponent: Component[ToComponentAlgebra, String] =
      Textbox.empty.widen[ToComponentAlgebra]
  }
}
