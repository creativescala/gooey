# Quick Start

To start using Gooey you need to import the core Gooey library, and a backend. Here I'll use the [Calico][calico] backend, so I can render the interface right here on this page.

```scala
import gooey.component.*
import gooey.syntax.all.*
```
```scala
import gooey.calico.{*, given}
import gooey.calico.syntax.all.*
import calico.unsafe.given
```

Now we need to create our user interface. Let's say I'm building a pet rating application. I want the name of the pet, and a rating from 10 to 14. I can directly translate this into Gooey.

```scala
val petRatingUi =
  Textbox.empty
    .withLabel("Pet name")
    .and(Slider(10, 14).withLabel("Rating"))
```

Now all I need to do is create my user interface with a specific backend, and then render the user interface. 

```scala
val calicoUi = petRatingUi.create
```
```scala
calicoUi.renderComponentToId("ui")
  .unsafeRunAndForget()
```

Calling `create` builds a backend specific representation using whichever backend is in the `given` scope. In this case it's the Calico backend which we imported earlier. We then use a backend specific method, `renderComponentToId`, to create the UI in the webpage as the position of the element with the given id (in this case the id is `ui`). Finally we run UI, using the Calico specific operation `unsafeRunAndForget`.

With that all done, we end up with the masterpiece below.

@:doodle(pet-rating, PetRating.mount)

[calico]: https://www.armanbilge.com/calico/
