Vistula is probably the first language that treats [Observables](http://reactivex.io/) as first-class citizens.
It aims at making web applications easier to write and more error-prone. It does that by encapsulation of event handling
(i.e. user interaction, AJAX) into code that embraces principles of functional programming.

## Core features

# Observables as first-class citizens

`clock` is an Observable holding current `Date` instance.

```
let clock = stdlib.time.clock
let secondsThisHour = clock.getMinutes() * 60 + clock.getSeconds()
let quarterPassed = secondsThisHour >= 900
```

`secondsThisHour`, is and **any variables that depend on it** will be automatically updated on every clock tick.

# Built-in template language

## State of development

Vistula is still in a very very early phase of development. Many features are missing,
there's a lot of bugs, performance issues etc.

## Inspirations

* (Elm)[http://elm-lang.org/], but treating observables as first-class citizens
came before (signals)[http://elm-lang.org/blog/farewell-to-frp].

## M1

* Classes
* Functions return declarations

* Dict as class
* Recursion
* Generators
* Subscribe to side-effects
* Declare functions after usage
* Functions overloading
* Imports, packages
* Pattern matching
* Generics
* Native stdlib
* JS bindings
