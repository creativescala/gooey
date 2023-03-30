package gooey.component

/** A marker trait for UI components. Indicates that a component can produce a
  * value of type `A`. By convention all components should extend this.
  */
trait Component[Alg <: Algebra, A] {
  def apply(algebra: Alg): algebra.UI[A]
}
