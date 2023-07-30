package gooey.generic

import gooey.component.*
import gooey.syntax.all.*
import magnolia1.*

type ToComponentAlgebra = And.Algebra & Text.Algebra & Textbox.Algebra

trait ToComponent[A] {
  extension (v: A) def toComponent: Component[ToComponentAlgebra, A]
}

object ToComponent extends AutoDerivation[ToComponent] {
  def join[A](ctx: CaseClass[ToComponent, A]): ToComponent[A] =
    value =>
      ctx.params
        .map { param =>
          param.typeclass.toComponent(param.deref(value))
        }
        .fold(_ and _)

  override def split[A](ctx: SealedTrait[ToComponent, A]): ToComponent[A] =
    value =>
      ctx.choose(value) { sub => sub.typeclass.toComponent(sub.cast(value)) }

  given ToComponent[String] = s => Textbox.empty.withDefault(s)
}
