package gooey

/** An interpreter constructs a UI of type `Output` from a description with type
  * given by `Input`
  */
trait Interpreter[Input[_], Output[_]] {
  def build[A](in: Input[A]): Output[A]
}
