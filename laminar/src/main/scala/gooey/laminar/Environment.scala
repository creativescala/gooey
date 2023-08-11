/*
 * Copyright 2023 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gooey.laminar

import com.raquo.airstream.state.{Var => LVar}
import com.raquo.laminar.api.L.*
import gooey.Var
import gooey.Var.Constant
import gooey.Var.View
import gooey.WritableVar

import scala.collection.mutable

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

  def get[A](id: Var.Id): Option[Sink[A]] =
    env.get(id).map(_.asInstanceOf[Sink[A]])

  def set[A](id: Var.Id, source: LVar[A]): Unit =
    env += (id -> source)
}
object Environment {
  def empty: Environment = Environment(mutable.Map.empty)
}
