package gooey.laminar

import com.raquo.laminar.api.L.{*, given}
import com.raquo.airstream.state.{Var => LVar}
import gooey.Var
import gooey.Display
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
import cats.data.Chain

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
    val signals = observers.map(o => env.addSource(o.id, output))
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

    LaminarComponent(element, output.toObservable)
  }
}
