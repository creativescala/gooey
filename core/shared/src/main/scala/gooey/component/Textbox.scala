package gooey.component

import gooey.Algebra
import gooey.component.style.TextboxStyle

final case class Textbox(
    label: Option[String],
    default: String,
    style: TextboxStyle
) extends Component[Textbox.Algebra, String],
      Labelable[Textbox],
      Styleable[Textbox, TextboxStyle] {
  def withLabel(label: String): Textbox =
    this.copy(label = Some(label))

  def withoutLabel: Textbox =
    this.copy(label = None)

  def withStyle(style: TextboxStyle): Textbox =
    this.copy(style = style)

  def withDefault(default: String): Textbox =
    this.copy(default = default)

  def apply(algebra: Textbox.Algebra): algebra.UI[String] =
    algebra.textbox(this)
}
object Textbox {
  trait Algebra extends gooey.Algebra {
    def textbox(t: Textbox): UI[String]
  }

  val empty: Textbox = Textbox(None, "", TextboxStyle.SingleLine)
}
