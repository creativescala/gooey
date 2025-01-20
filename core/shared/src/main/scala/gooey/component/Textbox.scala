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
import gooey.component.style.TextboxStyle

final case class Textbox(
    label: Option[String],
    default: String,
    style: TextboxStyle,
    observers: Chain[WritableVar[String]]
) extends Component[Textbox.Algebra, String],
      Labelable[Textbox],
      Styleable[Textbox, TextboxStyle] {
  def withLabel(label: String): Textbox =
    this.copy(label = Some(label))

  def withoutLabel: Textbox =
    this.copy(label = None)

  def withObserver(writable: WritableVar[String]): Textbox =
    this.copy(observers = writable +: observers)

  def withStyle(style: TextboxStyle): Textbox =
    this.copy(style = style)

  def withDefault(default: String): Textbox =
    this.copy(default = default)

  private[gooey] def build(algebra: Textbox.Algebra)(
      env: algebra.Env
  ): algebra.UI[String] =
    algebra.textbox(label, default, style, observers)(env)
}
object Textbox {
  trait Algebra extends gooey.Algebra {
    def textbox(
        label: Option[String],
        default: String,
        style: TextboxStyle,
        observers: Chain[WritableVar[String]]
    )(env: Env): UI[String]
  }

  val empty: Textbox = Textbox(None, "", TextboxStyle.SingleLine, Chain.empty)
}
