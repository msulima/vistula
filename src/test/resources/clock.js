var timer = new vistula.ObservableImpl();
setInterval(timer.rxPush.bind(timer), 1000);

var clock = timer.rxMap(function () {
    return new Date().getTime();
});

var cursorX = new vistula.ObservableImpl();
var cursorY = new vistula.ObservableImpl();

document.addEventListener("mousemove", function (event) {
    cursorX.rxPush(event.screenX);
    cursorY.rxPush(event.screenY);
});

var cursor = vistula.constantObservable({
    x: cursorX,
    y: cursorY
});

let stdlib = vistula.toObservable({
    dom: {
        appendChild: appendChild
    },
    net: {
        ajaxGet: ajaxGet
    }
});

/*-----*/
let start = vistula.constantObservable(new Date().getTime());
;
var ticks = vistula.aggregate(vistula.constantObservable(0), clock, ($acc, $source) => {
    let ticks = vistula.constantObservable($acc);
    let clock = vistula.constantObservable($source);
    return ticks.rxMap(function ($arg) {
        return $arg + 1;
    });
});
function oddTime(clock) {
    return vistula.ifStatement(clock.rxMap(function ($arg) {
        return $arg % 2;
    }).rxMap(function ($arg) {
        return $arg == 0;
    }), clock, vistula.constantObservable("no"));
};
function realTimeElapsed(elapsed) {
    return vistula.zip([
        clock,
        elapsed
    ]).rxMap(function ($args) {
        return $args[0] - $args[1];
    });
};
var timeElapsed = vistula.zip([
    clock,
    start
]).rxMap(function ($args) {
    return $args[0] - $args[1];
});
var labelText = vistula.zip([
    vistula.zip([
        vistula.zip([
            vistula.zip([
                clock.rxMap(function ($arg) {
                    return "It is: " + $arg;
                }).rxMap(function ($arg) {
                    return $arg + ", elapsed from entering page: ";
                }),
                timeElapsed
            ]).rxMap(function ($args) {
                return $args[0] + $args[1];
            }).rxMap(function ($arg) {
                return $arg + " in ";
            }),
            ticks
        ]).rxMap(function ($args) {
            return $args[0] + $args[1];
        }).rxMap(function ($arg) {
            return $arg + " real: ";
        }),
        realTimeElapsed(start)
    ]).rxMap(function ($args) {
        return $args[0] + $args[1];
    }).rxMap(function ($arg) {
        return $arg + " is odd: ";
    }),
    oddTime(clock)
]).rxMap(function ($args) {
    return $args[0] + $args[1];
});
var areaField = vistula.zip([
    cursor.rxFlatMap(function ($arg) {
        return $arg.x;
    }),
    cursor.rxFlatMap(function ($arg) {
        return $arg.y;
    })
]).rxMap(function ($args) {
    return $args[0] * $args[1];
});
var main = vistula.zipAndFlatten([
    vistula.dom.createElement(document.createElement("div"), [
        vistula.dom.textNode("\n  "),
        vistula.dom.createElement(document.createElement("p"), [
            vistula.dom.textObservable(labelText)
        ]),
        vistula.dom.textNode("\n  "),
        vistula.dom.createElement(document.createElement("p"), [
            vistula.dom.textNode("\n  "),
            vistula.dom.ifStatement(areaField.rxMap(function ($arg) {
                return $arg < 160000;
            }), [
                vistula.dom.textNode("Sorry, area is "),
                vistula.dom.createElement(document.createElement("strong"), [
                    vistula.dom.textNode("too small")
                ]),
                vistula.dom.textNode(".\n  ")
            ], [
                vistula.dom.textNode("Area is "),
                vistula.dom.textObservable(cursor.rxFlatMap(function ($arg) {
                    return $arg.x;
                })),
                vistula.dom.textNode(" * "),
                vistula.dom.textObservable(cursor.rxFlatMap(function ($arg) {
                    return $arg.y;
                })),
                vistula.dom.textNode(" = "),
                vistula.dom.textObservable(areaField),
                vistula.dom.textNode(" px^2\n  ")
            ]),
            vistula.dom.textNode("\n  ")
        ]),
        vistula.dom.textNode("\n  "),
        vistula.dom.createElement(document.createElement("ul"), [
            vistula.dom.textNode("\n    "),
            stdlib.rxFlatMap(function ($arg) {
                return $arg.net;
            }).rxFlatMap(function ($arg) {
                return $arg.ajaxGet;
            }).rxFlatMap(function ($arg) {
                return $arg(vistula.constantObservable("http://uinames.com/api/?amount=3"));
            }).rxFlatMap(function ($arg) {
                return vistula.zipAndFlatten($arg.map(function (person) {
                    return vistula.zipAndFlatten([
                        vistula.dom.createElement(document.createElement("li"), [
                            vistula.dom.textObservable(person.rxFlatMap(function ($arg) {
                                return $arg.name;
                            })),
                            vistula.dom.textNode(" from "),
                            vistula.dom.textObservable(person.rxFlatMap(function ($arg) {
                                return $arg.region;
                            }))
                        ]),
                        vistula.dom.textNode("\n    ")
                    ]);
                }))
            }),
            vistula.dom.textNode("\n  ")
        ]),
        vistula.dom.textNode("\n")
    ]),
    vistula.dom.textNode("\n")
]);
stdlib.rxFlatMap(function ($arg) {
    return $arg.dom;
}).rxFlatMap(function ($arg) {
    return $arg.appendChild;
}).rxFlatMap(function ($arg) {
    return $arg(vistula.constantObservable("main"), main);
});
/*-----*/

function appendChild(Target, Observables) {
    return vistula.zip([Target, Observables]).rxMap(function ($args) {
        var target = document.getElementById($args[0]);
        $args[1].forEach(function (obs) {
            target.appendChild(obs);
        });
    });
}

function ajaxGet(Url) {
    var obs = new vistula.ObservableImpl();

    return Url.rxFlatMap(function (url) {
        var request = new XMLHttpRequest();
        request.onreadystatechange = function () {
            var DONE = this.DONE || 4;
            if (this.readyState === DONE) {
                var value = vistula.toObservable(JSON.parse(this.responseText));
                value.rxForEach(obs.rxPush.bind(obs));
            }
        };
        request.open('GET', url, true);
        request.send(null);

        return obs;
    });
}
