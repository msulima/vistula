var ObservableImpl = vistula.ObservableImpl;
var Zip = vistula.Zip;
var ConstantObservable = vistula.ConstantObservable;

var timer = new ObservableImpl();
setInterval(timer.onNext.bind(timer), 1000);

var clock = timer.map(function () {
    return new Date().getTime();
});

var ticks_acc = 0;
var ticks = clock.map(function () {
    ticks_acc = ticks_acc + 1;
    return ticks_acc;
});

var start = ConstantObservable(new Date().getTime());

/*-----*/
function oddTime(clock) {
    return clock.map(function ($arg) {
        return $arg % 2;
    }).map(function ($arg) {
        return $arg == 0;
    }).flatMap(function ($ifCondition) {
        if ($ifCondition) {
            return clock;
        } else {
            return ConstantObservable("no");
        }
    });
}
function realTimeElapsed(elapsed) {
    return Zip([clock, elapsed]).map(function ($args) {
        return $args[0] - $args[1];
    });
}
var timeElapsed = Zip([clock, start]).map(function ($args) {
    return $args[0] - $args[1];
});
var labelText = Zip([Zip([Zip([Zip([clock.map(function ($arg) {
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