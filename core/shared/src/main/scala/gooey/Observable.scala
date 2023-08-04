package gooey

/** An observable is a time varying value of type A, or a constant value of type
  * A.
  */
type Observable[A] = A | Var[A]
object Observable {
  def toVar[A](observable: Observable[A]): Var[A] =
    observable match {
      case v: Var[A] => v
      case other: A  => Var.constant(other)
    }
}
