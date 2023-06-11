package gooey.component

/** Create a component that combines two child components where the first is
  * laid out beside the second.
  */
final case class Beside[Alg1 <: gooey.Algebra, Alg2 <: gooey.Algebra, A, B](
    left: Component[Alg1, A],
    right: Component[Alg2, B]
) extends Component[Alg1 & Alg2 & Beside.Algebra, (A, B)] {
  def create(using algebra: Alg1 & Alg2 & Beside.Algebra): algebra.UI[(A, B)] =
    algebra.beside(left.create, right.create)
}
object Beside {
  trait Algebra extends gooey.Algebra {
    def beside[A, B](left: UI[A], right: UI[B]): UI[(A, B)]
  }
}
