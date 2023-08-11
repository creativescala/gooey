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

package gooey.laminar

import cats.data.Chain
import com.raquo.airstream.state.{Var => LVar}
import com.raquo.laminar.api.L.*
import gooey.Display
import gooey.Var
import gooey.WritableVar
import gooey.component.And
import gooey.component.Checkbox
import gooey.component.Dropdown
import gooey.component.Form
import gooey.component.Map
import gooey.component.Observe
import gooey.component.Pure
import gooey.component.Slider
import gooey.component.Text
import gooey.component.Textbox
import gooey.component.style.*

type LaminarAlgebra =
  gooey.Algebra & And.Algebra & Checkbox.Algebra & Dropdown.Algebra &
    Form.Algebra & Map.Algebra & Observe.Algebra & Pure.Algebra &
    Slider.Algebra & Text.Algebra & Textbox.Algebra

given LaminarAlgebra: gooey.Algebra
  with Text.Algebra
  with Textbox.Algebra
  with {
  type Env = Environment
  type UI[A] = LaminarComponent[A]

  def initialize(): Env = Environment.empty

  def text(content: Var[String], theDisplay: Var[Display])(
      env: Environment
  ): LaminarComponent[Unit] = {
    val c = env.getOrCreate(content)
    val d = env.getOrCreate(theDisplay).map {
      case Display.Show => display.block.value
      case Display.Hide => display.none.value
    }
    val element = p(display <-- d, child.text <-- c)
    val output = Val(())

    LaminarComponent(element, output)
  }

  def textbox(
      label: Option[String],
      default: String,
      style: TextboxStyle,
      observers: Chain[WritableVar[String]],
      display: Var[Display]
  )(env: Environment): LaminarComponent[String] = {
    val output = LVar(default)
    val signals: List[(Var.Id, Option[Sink[String]])] =
      observers.map(o => (o.id -> env.get(o.id))).toList

    // Hook up the observers that already exist in the environment to the onInput event
    val modifiers = signals.collect {
      case (id -> Some(sink)) => (onInput.mapToValue --> sink)
    }
    // Set the IDs that don't have an observer to output
    signals
      .collect { case (id -> None) => id }
      .foreach(id => env.set(id, output))

    // val signals = observers.map(o => env.addSource(o.id, output))
    val element =
      style match {
        case TextboxStyle.SingleLine =>
          input(
            value := default,
            `type` := "text",
            onInput.mapToValue --> output.writer
          )
        case TextboxStyle.MultiLine =>
          textArea(value := default)
      }

    LaminarComponent(element.amend(modifiers: _*), output.toObservable)
  }
}
