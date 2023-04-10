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
import gooey.calico.*
import gooey.component.*
import gooey.syntax.all.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("BasicCalico")
object BasicCalico {
  @JSExport
  def mount(id: String): Unit = {
    val rootElement =
      Window[IO].document.getElementById(id).map(_.get)
    rootElement.flatMap(render.renderInto(_).useForever).unsafeRunAndForget()
  }

  def render: Resource[IO, HtmlElement[IO]] =
    Checkbox.empty
      .withLabel("Is this awesome?")
      .above(
        Textbox.empty.withLabel(
          "Describe, in your own words, the amount of awesomeness"
        )
      )(Algebra)
      .map(_.element)
}
