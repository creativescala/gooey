package gooey.component

final case class Textbox(label: Option[String], default: String)
    extends Component[Boolean],
      Label[Textbox] {
  def withLabel(label: String): Textbox =
    this.copy(label = Some(label))

  def withoutLabel: Textbox =
    this.copy(label = None)

  def withDefault(default: String): Textbox =
    this.copy(default = default)
}
object Textbox {
  val empty: Textbox = Textbox(None, "")
}
