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
import gooey.Algebra
import gooey.WritableVar

final case class Slider(
    label: Option[String],
    min: Int,
    max: Int,
    default: Int,
    observers: Chain[WritableVar[Int]]
) extends Component[Slider.Algebra, Int],
      Labelable[Slider] {
  def withLabel(label: String): Slider =
    this.copy(label = Some(label))

  def withoutLabel: Slider =
    this.copy(label = None)

  def withDefault(default: Int): Slider = {
    Slider.validate(min, max, default)
    this.copy(default = default)
  }

  def withMinimum(min: Int): Slider = {
    Slider.validate(min, max, default)
    this.copy(min = min)
  }

  def withMaximum(max: Int): Slider = {
    Slider.validate(min, max, default)
    this.copy(max = max)
  }

  def withObserver(writable: WritableVar[Int]): Slider =
    this.copy(observers = writable +: observers)

  private[gooey] def build(algebra: Slider.Algebra)(
      env: algebra.Env
  ): algebra.UI[Int] =
    algebra.slider(label, min, max, default, observers)(env)
}
object Slider {
  trait Algebra extends gooey.Algebra {
    def slider(
        label: Option[String],
        min: Int,
        max: Int,
        default: Int,
        observers: Chain[WritableVar[Int]]
    )(env: Env): UI[Int]
  }

  def validate(min: Int, max: Int, default: Int): Unit = {
    assert(
      min <= max,
      s"The Slider's minimum value must be less than or equal to the maximum value, but was given ${min} which is not less than or equal to ${max}."
    )
    assert(
      min <= default && default <= max,
      s"The Slider's default value must be between the minimum and maximum value, but was given ${default} which is not between ${min} and ${max}."
    )
  }

  /** Create a slider with a minimum value of 0, a maximum of 100, and a default
    * value of 50.
    */
  val default: Slider = Slider(None, 0, 100, 50, Chain.empty)

  /** Create a slider with the given minimum and maximum value, and a default
    * value halfway between the two.
    */
  def apply(min: Int, max: Int): Slider =
    apply(min, max, min + ((max - min) / 2))

  def apply(min: Int, max: Int, value: Int): Slider = {
    validate(min, max, value)
    Slider(None, min, max, value, Chain.empty)
  }
}
