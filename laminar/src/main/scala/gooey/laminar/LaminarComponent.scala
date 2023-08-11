package gooey.laminar

import com.raquo.laminar.api.L.*

final case class LaminarComponent[A](element: HtmlElement, signal: Signal[A])
