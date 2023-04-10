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

package gooey.calico.syntax

import calico.syntax.*
import cats.effect.*
import fs2.concurrent.*
import fs2.dom.*
import gooey.calico.*

object all {
  extension [A](component: UI[A]) {
    def renderIntoId(id: String)(using Dom[IO]): IO[Signal[IO, A]] = {
      val rootElement: IO[Element[cats.effect.IO]] =
        Window[IO].document.getElementById(id).map(_.get)
      for {
        elt <- rootElement
        output <- (for {
          c <- component
          _ <- Resource.make(elt.appendChild(c.element))(_ =>
            elt.removeChild(c.element)
          )
        } yield c.output).useForever
      } yield output
    }
  }
}
