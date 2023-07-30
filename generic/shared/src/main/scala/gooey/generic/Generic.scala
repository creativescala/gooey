package gooey.generic

import gooey.component.*
import magnolia1.*

trait ToComponent[A] {
  extension (v: A) def toComponent: Component[Textbox.Algebra & And.Algebra, A]
}

object ToComponent extends AutoDerivation[ToComponent] {
  def join[A](ctx: CaseClass[ToComponent, A]): ToComponent[A] = value =>
    ctx.params
      .map { param =>
        param.typeclass.toComponent(param.deref(value))
      }
      .mkString(s"${ctx.typeInfo.short}(", ",", ")")

  override def split[A](ctx: SealedTrait[ToComponent, A]): ToComponent[A] =
    value => ctx.choose(value) { sub => sub.typeclass.print(sub.cast(value)) }

  given ToComponent[String] = s => Textbox.empty.withDefault(s)
}
