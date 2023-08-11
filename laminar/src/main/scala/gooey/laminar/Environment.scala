package gooey.laminar

import com.raquo.laminar.api.L.{*, given}
import com.raquo.airstream.state.{Var => LVar}
import gooey.Var
import gooey.Var.Constant
import gooey.Var.View
import gooey.WritableVar

import scala.collection.mutable
import com.raquo.airstream.ownership.Owner

final case class Environment(
    env: mutable.Map[Var.Id, LVar[?]]
) {
  def getOrCreate[A](proxy: Var[A]): Signal[A] = {
    def getOrUse[B](id: Var.Id, default: => LVar[B]): Signal[B] =
      env
        .get(id)
        .fold {
          val s = default
          env += (id -> s)
          s.toObservable
        }(value => value.asInstanceOf[Signal[B]])

    def loop[B](proxy: Var[B]): Signal[B] =
      proxy match {
        case View(source, f)     => loop(source).map(f)
        case c @ Constant(value) => getOrUse(c.id, LVar(value))
        case w @ WritableVar(default) =>
          getOrUse(w.id, LVar(default))
      }

    loop(proxy)
  }

  def addSource[A](id: Var.Id, source: LVar[A])(using Owner): Signal[A] =
    env.get(id) match {
      case None =>
        env += (id -> source)
        source.toObservable
      case Some(value) =>
        // We already have a signal with the given ID. Push values from this
        // existing value to the source.
        value.asInstanceOf[LVar[A]].toObservable.foreach(a => source.set(a))
        source.toObservable
    }
}
object Environment {
  def empty: Environment = Environment(mutable.Map.empty)
}
