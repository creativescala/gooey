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

package gooey.swing

import cats.data.Chain
import cats.effect.IO
import cats.effect.Resource
import cats.effect.syntax.all.*
import cats.syntax.all.*
import fs2.concurrent.*
import gooey.component.And
import gooey.component.Checkbox
import gooey.component.Map
import gooey.component.Pure
import gooey.component.Textbox
import gooey.component.style.*

import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

given Algebra: gooey.Algebra
  with And.Algebra
  with Checkbox.Algebra
  with Map.Algebra
  with Pure.Algebra
  with Textbox.Algebra
  with {

  type UI[A] = Resource[IO, Component[A]]

  def makeComponent(label: JLabel, element: JComponent): JPanel = {
    val panel = JPanel()
    panel.setLayout(BoxLayout(panel, BoxLayout.X_AXIS))

    label.setLabelFor(element)

    panel.add(label)
    panel.add(element)

    panel
  }

  def makeLabel(theLabel: Option[String]): JLabel =
    theLabel.fold(JLabel()) { l => JLabel(l) }

  def and[A, B](f: UI[A], s: UI[B]): UI[(A, B)] = {
    for {
      fst <- f
      snd <- s
    } yield fst.product(snd)
  }

  def checkbox(label: Option[String], default: Boolean): UI[Boolean] = {
    //   SignallingRef[IO].of(default).toResource.flatMap { output =>
    //     val component =
    //       makeComponent(
    //         makeLabel(label),
    //         input.withSelf { self =>
    //           (
    //             `type` := "checkbox",
    //             cls := checkboxClass,
    //             checked := default,
    //             onChange --> (_.foreach { _ =>
    //               output.getAndUpdate(v => !v).void
    //             })
    //           )
    //         }
    //       )
    //     Component(component, output)
    //   }
    ???
  }

  def map[A, B](source: UI[A], f: A => B): UI[B] =
    source.map(component => component.map(f))

  def pure[A](value: A): UI[A] = {
    val component = Component(Chain.empty, Signal.constant[IO, A](value))
    Resource.eval(IO.pure(component))
  }

  def textbox(
      label: Option[String],
      default: String,
      style: TextboxStyle
  ): UI[String] = {
    SignallingRef[IO].of(default).toResource.map { output =>
      val component =
        makeComponent(
          makeLabel(label),
          style match {
            case TextboxStyle.SingleLine =>
              val element = JTextField(default)
              element.addActionListener(evt => output.set(element.getText()))
              element

            case TextboxStyle.MultiLine =>
              val element = JTextArea(default)
              element
                .getDocument()
                .addDocumentListener(
                  new DocumentListener {
                    def changedUpdate(e: DocumentEvent): Unit = {
                      output.set(element.getText())
                    }
                    def insertUpdate(e: DocumentEvent): Unit = {
                      output.set(element.getText())
                    }
                    def removeUpdate(e: DocumentEvent): Unit = {
                      output.set(element.getText())
                    }
                  }
                )
              element
          }
        )
      Component(component, output)
    }
  }

}
