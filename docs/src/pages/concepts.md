# Concepts

The understand Gooey you should understand the core concepts described here.


## Components and Algebras

A @:api(gooey.component.Component) is the core building block for user interfaces. It represents an element of a user interface, such as a button or a section of text, or compound component that are created from smaller components, such as a layout components.

The `Component` type is defined as 

```scala
trait Component[-Alg <: Algebra, A]
```

The type parameter `Alg` defines the functionality needed by the component, such as the ability to display text boxes or place components beside one another. These algebras are what allows Gooey to work cross-platform without being lowest common denominator: an algebra can be specific to a particular backend.

The type `A` defines the type of values that the `Component` produces. So, for example, a `Textbox` component produces a `String`; the `String` the user enters in the interface.


## Builders

The majority of methods on components are *builders*. This means the return a copy of the component they operate on. Builder methods *always* return an updated copy of the component; they *never* mutate a component. This allows reasoning with substitution about components. For example

```scala mdoc
import gooey.component.*

val c1 = Textbox.empty.withLabel("A label")
val c2 = Textbox.empty.withLabel("A label")
```

is exactly the same as

```scala mdoc:silent:reset
import gooey.component.*

val c1 = Textbox.empty.withLabel("A label")
val c2 = c1
```

You can tell a builder method by the name of the method. The name of a  builder method will start with

* `with`, such as `withLabel` above, for a method that changes a simple property of a component;
* `add`, for a method that adds an element to a collection of elements within a component; or
* `for`, for a method the allows updating a property that itself contains properties you may with to change.


## Narrow and Wide Methods

Narrow methods the same type the operate on. Wide methods return a `Component`. Builder methods are always narrow.


## Observables

There are two structures in a user interface: the structure of the values that flow through the interface, and the structure of the layout of the interface. 

In simple user interfaces these structures are the same. These simple interfaces can be built just by composing components. In more complex interfaces these structures differ. To support these different structures Gooey provides the concept of observables. Observables allow you to model the value flow using a structure that differs from the layout of the components.
