var timer = new vistula.ObservableImpl();
setInterval(timer.onNext.bind(timer), 1000);

var clock = timer.map(function () {
    return new Date().getTime();
});

var start = vistula.constantObservable(new Date().getTime());

/*-----*/
var ticks = vistula.aggregate(vistula.constantObservable(0), clock, ($acc, $source) => {
    let ticks = vistula.constantObservable($acc);
    let clock = vistula.constantObservable($source);
    return ticks.map(function ($arg) {
        return $arg + 1;
    });
});
function oddTime(clock) {
    return clock.map(function ($arg) {
        return $arg % 2;
    }).map(function ($arg) {
        return $arg == 0;
    }).flatMap(function ($ifCondition) {
        if ($ifCondition) {
            return clock;
        } else {
            return vistula.constantObservable("no");
        }
    });
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
/*-----*/

labelText.forEach(function (text) {
    document.getElementById("text").textContent = text;
});