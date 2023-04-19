package gooey.swing

import cats.data.Chain
import cats.effect.IO
import cats.syntax.all.*
import fs2.concurrent.Signal
import javax.swing.JComponent

final case class Component[A](
    components: Chain[JComponent],
    signal: Signal[IO, A]
) {
  def map[B](f: A => B): Component[B] =
    this.copy(signal = signal.map(f))

  def product[B](that: Component[B]): Component[(A, B)] =
    Component(
      this.components ++ that.components,
      (this.signal, that.signal).tupled
    )
}
object Component {
  def apply[A](component: JComponent, signal: Signal[IO, A]): Component[A] =
    Component(Chain(component), signal)
}
