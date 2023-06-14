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

package gooey.component

import gooey.Algebra

/** A Form is a utility to quickly collect information from the user. It trades
  * off flexibility for ease-of-use.
  *
  *   - `title` is the title of the form.
  *   - `component` is a `Component` that specifies the information to collect.
  *   - `submit` is the text on the button that submits the form.
  *   - `onSubmit` function is the action that takes place when the form is
  *     submitted.
  *
  * A Form is not a Component because it is designed to contain components, but
  * not be composed with other components.
  */
final case class Form[Alg <: Algebra, A](
    title: String,
    component: Component[Alg, A],
    submit: String,
    onSubmit: A => Unit
) {

  def withComponent[Alg2 <: Algebra](
      component: Component[Alg2, A]
  ): Form[Alg2, A] =
    this.copy(component = component)

  def withOnSubmit(onSubmit: A => Unit): Form[Alg, A] =
    this.copy(onSubmit = onSubmit)

  def withSubmit(submit: String): Form[Alg, A] =
    this.copy(submit = submit)

  def withTitle(title: String): Form[Alg, A] =
    this.copy(title = title)

  final def create(using algebra: Alg & Form.Algebra): algebra.UI[A] =
    build(algebra)(algebra.initialize())

  private[gooey] def build(algebra: Alg & Form.Algebra)(
      env: algebra.Env
  ): algebra.UI[A] =
    algebra.form(title, component.build(algebra)(env), submit, onSubmit)(env)
}
object Form {
  trait Algebra extends gooey.Algebra {
    def form[A](
        title: String,
        component: UI[A],
        submit: String,
        onSubmit: A => Unit
    )(env: Env): UI[A]
  }

  def apply[Alg <: Algebra, A](
      title: String,
      component: Component[Alg, A]
  ): Form[Alg, A] =
    Form(title, component, "Submit", _ => ())
}
