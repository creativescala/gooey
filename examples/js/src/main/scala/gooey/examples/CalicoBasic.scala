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
import cats.data.NonEmptySeq
import cats.effect.*
import cats.syntax.all.*
import fs2.dom.*
import gooey.Var
import gooey.calico.syntax.all.*
import gooey.calico.{_, given}
import gooey.component.*
import gooey.component.style.*
import gooey.syntax.all.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("CalicoBasic")
object CalicoBasic {
  @JSExport
  def mount(id: String): Unit = {

    val awesomeness = Var.writable[Boolean]
    val rating = Var.writable[Int]
    val adjective = Var.writable[Int]
    val reasons = Var.writable[String]

    val component = Text(
      "This example demonstrates the components implemented by the Calico backend."
    ).as[Algebra] *>
      (
        Checkbox.empty
          .withLabel("Is this awesome?")
          .withObserver(awesomeness)
          .as[Algebra],
        Slider(1, 10)
          .withLabel(
            "On a scale of 1 to 10, rate the amount of awesomeness"
          )
          .withObserver(rating)
          .as[Algebra],
        Dropdown(
          NonEmptySeq.of(("Superb", 1), ("Stupendous", 3), ("Awesome", 5))
        )
          .withLabel("Chose the adjective that best describes your experience")
          .withObserver(adjective)
          .as[Algebra],
        Textbox.empty
          .withLabel(
            "Describe, in your own words, the reasons behind your rating"
          )
          .withObserver(reasons)
          .withStyle(TextboxStyle.SingleLine)
          .as[Algebra],
      ).tupled
      <* Text(
        awesomeness.map(a =>
          if a then "Awesomeness is over 9000!"
          else "Awesomeness needs improvement"
        )
      ).and(
        Text(rating.map(r => s"The awesomeness rating is $r"))
      ).and(
        Text(adjective.map(r => s"The subjective adjective rating is $r"))
      ).and(
        Text(reasons.map(s => s"Reasons given are: $s"))
      )

    component.create
      .renderComponentToId(id)
      .unsafeRunAndForget()
  }
}
