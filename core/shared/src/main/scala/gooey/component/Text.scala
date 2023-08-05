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

import gooey.*

/** Text is a component for displaying text. */
final case class Text(content: Var[String], display: Var[Display])
    extends Component[Text.Algebra, Unit],
      Displayable[Text] {

  def withContent(content: String): Text =
    this.copy(content = Var.constant(content))

  def withContent(content: Var[String]): Text =
    this.copy(content = content)

  def withDisplay(display: Observable[Display]): Text =
    this.copy(display = Observable.toVar(display))

  private[gooey] def build(algebra: Text.Algebra)(
      env: algebra.Env
  ): algebra.UI[Unit] =
    algebra.text(content, display)(env)
}
object Text {
  trait Algebra extends gooey.Algebra {
    def text(content: Var[String], display: Var[Display])(
        env: Env
    ): UI[Unit]
  }

  def apply(content: Observable[String]): Text =
    Text(Observable.toVar(content), Var.constant(Display.Show))
}
