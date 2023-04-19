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
import calico.html.io.{_, given}
import calico.syntax.*
import calico.unsafe.given
import cats.effect.*
import fs2.dom.*
import gooey.calico.syntax.all.*
import gooey.calico.{_, given}
import gooey.component.*
import gooey.component.style.*
import gooey.syntax.all.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("BasicCalico")
object BasicCalico {
  @JSExport
  def mount(id: String): Unit = {
    Checkbox.empty
      .withLabel("Is this awesome?")
      .and(
        Textbox.empty
          .withLabel(
            "Describe, in your own words, the amount of awesomeness"
          )
          .withStyle(TextboxStyle.SingleLine)
      )
      .create
      .flatMap { c => c.build }
      .flatMap { case (elt, signal) =>
        div(
          elt,
          p(
            "Awesomeness ",
            signal.map((a, _) =>
              if a then "is over 9000" else "needs improving"
            )
          ),
          p("Reasons given are ", signal.map((_, r) => r))
        )
      }
      .renderIntoId(id)
      .unsafeRunAndForget()
  }
}