package gooey.component

/** Create a component that combines two child components where the first is
  * laid out above the second.
  */
final case class Above[Alg1 <: gooey.Algebra, Alg2 <: gooey.Algebra, A, B](
    top: Component[Alg1, A],
    bottom: Component[Alg2, B]
) extends Component[Alg1 & Alg2 & Above.Algebra, (A, B)] {
  def create(using algebra: Alg1 & Alg2 & Above.Algebra): algebra.UI[(A, B)] =
    algebra.above(top.create, bottom.create)
}
object Above {
  trait Algebra extends gooey.Algebra {
    def above[A, B](t: UI[A], b: UI[B]): UI[(A, B)]
  }
}
