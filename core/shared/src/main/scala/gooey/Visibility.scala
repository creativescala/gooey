package gooey

/** Enumeration for specifying if a Component is shown or hidden */
enum Visibility {
  case Visible
  case Invisible
}
object Visibility {

  /** Utility to convert a Boolean to a Visible, under the assumption that true
    * means visible.
    */
  def fromBoolean(visible: Boolean): Visibility =
    if visible then Visible else Invisible
}
