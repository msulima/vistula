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
    return vistula.zip([clock, elapsed]).map(function ($args) {
        return $args[0] - $args[1];
    });
}
var timeElapsed = vistula.zip([clock, start]).map(function ($args) {
    return $args[0] - $args[1];
});
var labelText = vistula.zip([vistula.zip([vistula.zip([vistula.zip([clock.map(function ($arg) {
    return "It is: " + $arg;
}).map(function ($arg) {
    return $arg + ", elapsed from entering page: ";
}), timeElapsed]).map(function ($args) {
    return $args[0] + $args[1];
}).map(function ($arg) {
    return $arg + " in ";
}), ticks]).map(function ($args) {
    return $args[0] + $args[1];
}).map(function ($arg) {
    return $arg + " real: ";
}), realTimeElapsed(start)]).map(function ($args) {
    return $args[0] + $args[1];
}).map(function ($arg) {
    return $arg + " is odd: ";
}), oddTime(clock)]).map(function ($args) {
    return $args[0] + $args[1];
});
var areaField = vistula.zip([cursor.flatMap(function ($arg) {
    return $arg.x;
}), cursor.flatMap(function ($arg) {
    return $arg.y;
})]).map(function ($args) {
    return $args[0] * $args[1];
});
var areaLabel = vistula.zip([vistula.zip([cursor.flatMap(function ($arg) {
    return $arg.x;
}).map(function ($arg) {
    return $arg + " * ";
}), cursor.flatMap(function ($arg) {
    return $arg.y;
})]).map(function ($args) {
    return $args[0] + $args[1];
}).map(function ($arg) {
    return $arg + " = ";
}), areaField]).map(function ($args) {
    return $args[0] + $args[1];
}).map(function ($arg) {
    return $arg + " px^2";
});
var x = vistula.dom.createElement(document.createElement("div"), [
    vistula.dom.createElement(document.createElement("p"), [
        vistula.dom.textObservable(labelText)
    ]),
    vistula.dom.createElement(document.createElement("p"), [
        vistula.dom.ifStatement(areaField.map(function ($arg) {
            return $arg < 160000;
        }), [vistula.dom.textNode("Sorry, area is "), vistula.dom.createElement(document.createElement("strong"), [
            vistula.dom.textNode("too small")
        ]), vistula.dom.textNode(".\n")], [vistula.dom.textNode("Area is "), vistula.dom.textObservable(areaLabel)])
    ])
]);
/*-----*/

x.forEach(function (x) {
    document.getElementById("main").appendChild(x[0]);
});
