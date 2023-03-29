package gooey

/** API for components that can have an attached label. */
trait Label[Self] { self: Self =>
  def label: Option[String]
  def withLabel(label: String): Self
  def withoutLabel: Self
}
