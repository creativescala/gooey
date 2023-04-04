package gooey

/** An Algebra defines an interface for some functionality that goes into
  * building a UI.
  */
trait Algebra {

  /** The type of the UI that an implementation of this algebra produces */
  type UI[A]
}
