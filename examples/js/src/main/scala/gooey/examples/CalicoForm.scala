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

package gooey.examples

import calico.*
import calico.html.io.given
import calico.unsafe.given
import cats.syntax.all.*
import gooey.calico.syntax.all.*
import gooey.calico.{_, given}
import gooey.component.Component.componentApplicative
import gooey.component.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("CalicoForm")
object CalicoForm {
  @JSExport
  def mount(id: String): Unit = {
    val component =
      (
        Textbox.empty.withLabel("What is your name?").widen[Algebra],
        Slider(1, 10)
          .withLabel(
            "On a scale of 1 (Chihuahua) to 10 (Great Dane), rate how much dog you have in you"
          )
          .widen[Algebra],
        Textbox.empty
          .withLabel(
            "What sort of philosophers are we, who know absolutely nothing about the origin and destiny of cats?"
          )
          .widen[Algebra]
      ).tupled

    val form =
      Form("Important Personal Information", component).withOnSubmit(
        (name, dog, philosophy) => {
          println(s"Name is $name")
          println(s"Amount of dog is $dog")
          println(s"Philosophy is $philosophy")
        }
      )

    form.create.renderComponentToId(id).unsafeRunAndForget()
  }
}
