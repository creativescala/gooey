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

package gooey.calico

import cats.effect.IO
import fs2.concurrent.Signal
import fs2.concurrent.SignallingRef
import gooey.Var
import gooey.Var.Constant
import gooey.Var.View
import gooey.WritableVar

import scala.collection.mutable

final case class Environment(
    env: mutable.Map[Var.Id, SignallingRef[IO, ?]]
) {
  def getOrCreate[A](proxy: Var[A]): IO[Signal[IO, A]] = {
    def getOrUse[B](id: Var.Id, default: => IO[SignallingRef[IO, B]]) =
      IO(env.get(id)).flatMap(opt =>
        opt match
          case None =>
            default.map { s =>
              env += (id -> s)
              s
            }
          case Some(value) => IO.pure(value.asInstanceOf[SignallingRef[IO, B]])
      )

    def loop[B](proxy: Var[B]): IO[Signal[IO, B]] =
      proxy match {
        case View(source, f)     => loop(source).map(s => s.map(f))
        case c @ Constant(value) => getOrUse(c.id, SignallingRef[IO].of(value))
        case w @ WritableVar(default) =>
          getOrUse(w.id, SignallingRef[IO].of(default))
      }

    loop(proxy)
  }

  def addSource[A](
      id: Var.Id,
      source: Signal[IO, A]
  ): IO[Signal[IO, A]] =
    IO(env.get(id)).flatMap(opt =>
      opt match
        case None =>
          val s: IO[SignallingRef[IO, A]] =
            source match {
              case s: SignallingRef[IO, A] =>
                IO.println("1) found signallingref") *> IO.pure(s)
              case other =>
                IO.println("1) found something else") *>
                  source.get
                    .flatMap(initial => SignallingRef.of(initial))
                    .flatMap(s =>
                      source.discrete
                        .evalMap(v =>
                          IO.println(s"1) pumping a value ${v}").as(v)
                        )
                        .foreach(v => s.set(v))
                        .compile
                        .drain
                        .as(s)
                    )
            }

          IO.println("1) not found") *>
            s.map { s =>
              env += (id -> s)
              s
            }
        case Some(value) =>
          val signal = value.asInstanceOf[SignallingRef[IO, A]]
          IO.println("2) found an existing value")
          source.discrete
            .evalMap(v => IO.println(s"2) pumping a value ${v}").as(v))
            .foreach(v => signal.set(v))
            .compile
            .drain
            .as(signal)
    )
}
object Environment {
  def empty: Environment = Environment(mutable.Map.empty)
}
