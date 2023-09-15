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
import gooey.all.*
import gooey.calico.syntax.all.*
import gooey.calico.{_, given}
import gooey.generic.*

import scala.scalajs.js.annotation.*

@JSExportTopLevel("CalicoGeneric")
object CalicoGeneric {
  final case class User(name: String, age: Int)

  val form1 = {
    given builder: ToComponent[User] = ToComponent.derived[User]
    Form.build[User]
  }

  val form2 = {
    given ToComponent[Int] =
      new ToComponent {
        def toComponent: Component[ToComponentAlgebra, scala.Int] =
          Textbox.empty.map(s => s.toInt)
      }
    given builder: ToComponent[User] = ToComponent.derived[User]
    Form.build[User]
  }

  @JSExport
  def mount1(id: String): Unit = {
    form1.create.renderComponentToId(id).unsafeRunAndForget()
  }
  @JSExport
  def mount2(id: String): Unit = {
    form2.create.renderComponentToId(id).unsafeRunAndForget()
  }
}
