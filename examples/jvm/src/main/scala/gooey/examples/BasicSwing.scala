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

import fs2.concurrent.Signal
import gooey.component.*
import gooey.component.style.*
import gooey.swing.given
import gooey.syntax.all.*
import net.bulbyvr.swing.io.IOSwingApp
import net.bulbyvr.swing.io.all.{*, given}

object BasicSwing extends IOSwingApp {
  def render =
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
      .flatMap { case (elt, signal) =>
        window(
          title := "Gooey",
          box(
            elt,
            label(
              text <-- signal.map((a, _) =>
                s"Awesomeness ${if a then "is over 9000" else "needs improving"}"
              )
            ),
            label(
              text <-- signal.map((_, r) => s"Reasons given are $r")
            )
          )
        )
      }
}
