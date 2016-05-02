var timer = new vistula.ObservableImpl();
setInterval(timer.onNext.bind(timer), 1000);

var clock = timer.map(function () {
    return new Date().getTime();
});

var cursorX = new vistula.ObservableImpl();
var cursorY = new vistula.ObservableImpl();

document.addEventListener("mousemove", function (event) {
    cursorX.onNext(event.screenX);
    cursorY.onNext(event.screenY);
});

var cursor = vistula.constantObservable({
    x: cursorX,
    y: cursorY
});

let stdlib = vistula.objectToObservable({
    dom: {
        appendChild: appendChild
    },
    net: {
        ajaxGet: ajaxGet
    }
});

/*-----*/
let start = vistula.constantObservable(new Date().getTime());

var ticks = vistula.aggregate(vistula.constantObservable(0), clock, ($acc, $source) => {
    let ticks = vistula.constantObservable($acc);
    let clock = vistula.constantObservable($source);
    return ticks.map(function ($arg) {
        return $arg + 1;
    });
});
function oddTime(clock) {
    return vistula.ifStatement(clock.map(function ($arg) {
        return $arg % 2;
    }).map(function ($arg) {
        return $arg == 0;
    }), clock, vistula.constantObservable("no"));
}
function realTimeElapsed(elapsed) {
    return vistula.zip([
        clock,
        elapsed
    ]).map(function ($args) {
        return $args[0] - $args[1];
    });
}
var timeElapsed = vistula.zip([
    clock,
    start
]).map(function ($args) {
    return $args[0] - $args[1];
});
var labelText = vistula.zip([
    vistula.zip([
        vistula.zip([
            vistula.zip([
                clock.map(function ($arg) {
                    return "It is: " + $arg;
                }).map(function ($arg) {
                    return $arg + ", elapsed from entering page: ";
                }),
                timeElapsed
            ]).map(function ($args) {
                return $args[0] + $args[1];
            }).map(function ($arg) {
                return $arg + " in ";
            }),
            ticks
        ]).map(function ($args) {
            return $args[0] + $args[1];
        }).map(function ($arg) {
            return $arg + " real: ";
        }),
        realTimeElapsed(start)
    ]).map(function ($args) {
        return $args[0] + $args[1];
    }).map(function ($arg) {
        return $arg + " is odd: ";
    }),
    oddTime(clock)
]).map(function ($args) {
    return $args[0] + $args[1];
});
var areaField = vistula.zip([
    cursor.flatMap(function ($arg) {
        return $arg.x;
    }),
    cursor.flatMap(function ($arg) {
        return $arg.y;
    })
]).map(function ($args) {
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
            vistula.dom.ifStatement(areaField.map(function ($arg) {
                return $arg < 160000;
            }), [
                vistula.dom.textNode("Sorry, area is "),
                vistula.dom.createElement(document.createElement("strong"), [
                    vistula.dom.textNode("too small")
                ]),
                vistula.dom.textNode(".\n  ")
            ], [
                vistula.dom.textNode("Area is "),
                vistula.dom.textObservable(cursor.flatMap(function ($arg) {
                    return $arg.x;
                })),
                vistula.dom.textNode(" * "),
                vistula.dom.textObservable(cursor.flatMap(function ($arg) {
                    return $arg.y;
                })),
                vistula.dom.textNode(" = "),
                vistula.dom.textObservable(areaField),
                vistula.dom.textNode(" px^2\n  ")
            ]),
            vistula.dom.textNode("\n  ")
        ]),
        vistula.dom.textNode("\n  "),
        vistula.dom.createElement(document.createElement("div"), [
            vistula.dom.textNode("\n    "),
            vistula.dom.textObservable(stdlib.flatMap(function ($arg) {
                return $arg.net;
            }).flatMap(function ($arg) {
                return $arg.ajaxGet;
            }).flatMap(function ($arg) {
                return $arg(vistula.constantObservable("http://uinames.com/api/?amount=3"));
            })),
            vistula.dom.textNode("\n  ")
        ]),
        vistula.dom.textNode("\n")
    ]),
    vistula.dom.textNode("\n")
]);
stdlib.flatMap(function ($arg) {
    return $arg.dom;
}).flatMap(function ($arg) {
    return $arg.appendChild;
}).flatMap(function ($arg) {
    return $arg(vistula.constantObservable("main"), main);
})
/*-----*/

function appendChild(Target, Observables) {
    return vistula.zip([Target, Observables]).map(function ($args) {
        var target = document.getElementById($args[0]);
        $args[1].forEach(function (obs) {
            target.appendChild(obs);
        });
    });
}

function ajaxGet(Url) {
    var obs = new vistula.ObservableImpl();

    return Url.flatMap(function (url) {
        var request = new XMLHttpRequest();
        request.onreadystatechange = function () {
            var DONE = this.DONE || 4;
            if (this.readyState === DONE) {
                obs.onNext(this.responseText)
            }
        };
        request.open('GET', url, true);
        request.send(null);

        return obs;
    });
}
