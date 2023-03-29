package gooey

final case class Checkbox(label: Option[String], default: Boolean)
    extends Component[Boolean],
      Label[Checkbox] {
  def withLabel(label: String): Checkbox =
    this.copy(label = Some(label))

  def withoutLabel: Checkbox =
    this.copy(label = None)

  def withDefault(default: Boolean): Checkbox =
    this.copy(default = default)
}
object Checkbox {
  val empty: Checkbox = Checkbox(None, false)
}
