Vistula is probably the first language that treats [Observables](http://reactivex.io/) as first-class citizens.
It aims at making web applications easier to write and more error-prone. It does that by encapsulation of event handling
(i.e. user interaction, AJAX) into code that embraces principles of functional programming.

## Core features

# Observables as first-class citizens

`clock` is an Observable holding current `Date` instance.

```
let clock = stdlib.time.clock
let secondsThisHour = clock.getMinutes() * 60 + clock.getSeconds()
```

`secondsThisHour`, is and **any variables that depend on it** will be automatically updated on every clock tick.

# Built-in template language

```html
<div>
  It's {{ (12 if (clock.getHours() % 12 == 0) else clock.getHours() % 12) }}:{{ clock.getMinutes() }}
  {% if clock.getHours() < 12 %}
    a.m.
  {% else %}
    p.m.
  {% endif %}
  <span>{{ secondsThisHour }} seconds passed this hour.</span>
</div>
```

*Yes, it even supports if-else!*

Templates are precompiled, which means they're type-safe and can be treated as regular objects.
They can access all variables/functions from scope in which they're defined.

```
def getHours() {
  if (clock.getHours() % 12 == 0) {
    12
  } else {
    clock.getHours() % 12
  }
}

let template = '''# html
<span>It's {{ getHours() }}:{{ clock.getMinutes() }}</span>
'''
```

# Automatic form bindings

```
let name = "You"

let template = '''# html
<input type="text" value="{{ name }}"/>
Hello {{ name }}
'''
```


## State of development

Vistula is still in a very very early phase of development. Many features are missing,
there's a lot of bugs, performance issues etc.

## Inspirations

* (Elm)[http://elm-lang.org/], but treating observables as first-class citizens
came before (signals)[http://elm-lang.org/blog/farewell-to-frp].
* (Smarty)[http://www.smarty.net/] and (Twirl)[https://www.playframework.com/documentation/2.4.x/ScalaTemplates] template engines.

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
