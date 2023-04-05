package gooey.component

final case class Above[Alg <: Above.Algebra, A, B](
    top: Component[Alg, A],
    bottom: Component[Alg, B]
) extends Component[Alg, (A, B)] {
  def apply(algebra: Alg): algebra.UI[(A, B)] =
    algebra.above(top(algebra), bottom(algebra))
}
object Above {
  trait Algebra extends gooey.Algebra {
    def above[A, B](t: UI[A], b: UI[B]): UI[(A, B)]
  }
}
