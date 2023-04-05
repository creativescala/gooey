package gooey.component

final case class Above[Alg <: Above.Algebra, A, B](
    top: Component[Alg, A],
    bottom: Component[Alg, B]
) extends Component[Alg, (A, B)] {
  def apply(algebra: Alg): algebra.UI[(A, B)] =
    algebra.above(this)
}
object Above {
  trait Algebra extends gooey.Algebra {
    def above[Alg <: Above.Algebra, A, B](a: Above[Alg, A, B]): UI[(A, B)]
  }
}
