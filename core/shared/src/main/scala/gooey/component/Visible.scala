package gooey.component

import gooey.Observable
import gooey.Visibility

/** API for components that can be shown or hidden. */
trait Visible[Self] { self: Self =>
  def withVisibility(visibility: Observable[Visibility]): Self
}
