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

package gooey.swing

import cats.data.Chain
import cats.effect.IO
import cats.syntax.all.*
import fs2.concurrent.Signal

import javax.swing.JComponent

final case class Component[A](
    components: Chain[JComponent],
    signal: Signal[IO, A]
) {
  def map[B](f: A => B): Component[B] =
    this.copy(signal = signal.map(f))

  def product[B](that: Component[B]): Component[(A, B)] =
    Component(
      this.components ++ that.components,
      (this.signal, that.signal).tupled
    )
}
object Component {
  def apply[A](component: JComponent, signal: Signal[IO, A]): Component[A] =
    Component(Chain(component), signal)
}
