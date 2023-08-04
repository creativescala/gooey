# Principles

The goal of Gooey is to make building user interfaces easy. There are many existing user interface toolkits, so this section lays out the principles that guide the development of Gooey and shows how it differs from other toolkits.

## Simple Things Are Easy

Above all, Gooey aims to make it really really easy to create simple user interfaces, such as forms to collect data. We believe that simple interfaces make up the vast majority of interfaces that developers create, particularly Scala developers who are likely to use Gooey. 

Gooey support complex interfaces in two ways:

* we provide reasonable customization that allows somewhat more complex interfaces to be defined within Gooey; and

* we can convert Gooey code into code for any supported backend, so you can develop the complex parts into a backend specific way.

If we succeed, you'll be able to create at least 80% of your user interface in Gooey, with a small amount of backend specific work for the more complex cases.


## Cross-platform But Not Lowest Common Denominator

Gooey is cross-platform. You can take a Gooey user interface and run it on the web, in a variety of different Scala.js frameworks, or run it natively on the JVM.

Cross-platform systems usually support the lowest common denominator between all supported systems. Gooey does *not* take this approach. It's design allows back-end specific extensions, so if you commit to a single back-end you can support arbitrary amounts of back-end specific options within Gooey.
