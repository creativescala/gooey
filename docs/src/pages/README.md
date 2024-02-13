# Gooey

Gooey is a Scala library for creating user interfaces. It's goal is to make it very easy to build user interfaces that are good enough for most uses. The tradeoff is that Gooey only allows limited customization. Gooey is also cross platform. Gooey user interfaces can run on the web or on the JVM, and work with a variety of user interface toolkits.


## Getting Started

To use Gooey, add the following to your `build.sbt`

```scala
resolvers ++= Resolver.sonatypeOssRepos("snapshots")
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
libraryDependencies += "org.creativescala" %% "gooey-core" % "@VERSION@"
=======
=======
>>>>>>> 5a28a2e (Document backend dependencies)
=======
>>>>>>> 5a28a2e (Document backend dependencies)

// Use this for JVM projects
libraryDependencies += "org.creativescala" %% "gooey-core" % "@VERSION@"

// Use this for Javascript / web projects, or projects targetting both the JVM and Javascript
libraryDependencies += "org.creativescala" %%% "gooey-core" % "@VERSION@"
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> 5a28a2e (Document backend dependencies)
=======
>>>>>>> 5a28a2e (Document backend dependencies)
=======
>>>>>>> 5a28a2e (Document backend dependencies)
```

You'll also need to add the particular backend you intend to use.

For the Calico backend:

``` scala
libraryDependencies += "org.creativescala" %%% "gooey-calico" % "@VERSION@"
```

For the Swing backend:

``` scala
libraryDependencies += "org.creativescala" %%% "gooey-swing" % "@VERSION@"
```


## ScalaDoc

See the ScalaDoc @:api(gooey.index) for API documentation.


## Related Work

- [gui-easy](https://docs.racket-lang.org/gui-easy/index.html) is a similar library for [Racket](https://www.racket-lang.org/) that targets native UIs.
- [Unicorn](https://github.com/art-w/unicorn) is an [O'Caml](https://ocaml.org/) library focused on the web.
- [Swing.IO](https://thedrawingcoder-gamer.github.io/Swing.IO/) is Calico for Swing, which is used our Swing backend
