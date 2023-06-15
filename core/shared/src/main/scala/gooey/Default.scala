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

package gooey

/** An arbitrary default value used to initialize writable vars. */
trait Default[A] {
  val default: A
}
object Default {
  given Default[String] with {
    val default = ""
  }

  given Default[Int] with {
    val default = 0
  }

  given Default[Double] with {
    val default = 0.0
  }

  given Default[Boolean] with {
    val default = true
  }

  given [A]: Default[Option[A]] with {
    val default = None
  }

  given [A]: Default[List[A]] with {
    val default = List.empty
  }
}
