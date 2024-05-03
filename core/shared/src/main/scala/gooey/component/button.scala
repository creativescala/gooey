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

final case class Button(
    label: Option[String],
    onClick: () => Unit,
    observers: Chain[WritableVar[Unit]]
) extends Component[Button.Algebra, Unit] {
    def withLabel(label: String): Button =
        this.copy(label = Some(label))

    def withoutLabel: Button =
        this.copy(label = "Click")

    def withOnClick(onClick: () => Unit): Button =
        this.copy(onClick = onClick)

    def withObserver(writable: WritableVar[String]): Button =
        this.copy(observers = writable +: observers)

    private[gooey] def build(algebra: Button.Algebra)(
        env: algebra.Env
    ): algebra.UI[Unit] =
      algebra.button(label, onClick, observers)(env)
}

object Button {
    trait Algebra extends gooey.Algebra {
        def button(
          label: Option[String],
          onClick: () => Unit,
          observers: Chain[WritableVar[Unit]]
        )(env: Env): UI[Unit]
    }

    val empty: Button = Button("", () => {}, Chain.empty)
}
