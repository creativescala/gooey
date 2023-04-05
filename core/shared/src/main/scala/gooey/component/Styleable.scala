package gooey.component

/** API for components that can have an attached style. */
trait Styleable[Self, Style] { self: Self =>
  def style: Style
  def withStyle(style: Style): Self
}
