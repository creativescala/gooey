package gooey.component

final case class Checkbox(label: Option[String], default: Boolean)
    extends Component[Checkbox.Algebra, Boolean],
      Label[Checkbox] {
  def withLabel(label: String): Checkbox =
    this.copy(label = Some(label))

  def withoutLabel: Checkbox =
    this.copy(label = None)

  def withDefault(default: Boolean): Checkbox =
    this.copy(default = default)

  def apply(algebra: Checkbox.Algebra): algebra.UI[Boolean] =
    algebra.checkbox(this)
}
object Checkbox {
  trait Algebra extends gooey.Algebra {
    def checkbox(c: Checkbox): UI[Boolean]
  }

  val empty: Checkbox = Checkbox(None, false)
}
