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
    return Zip([Zip([clock]).map(function ($args) {
        return $args[0] % 2;
    })]).map(function ($args) {
        return $args[0] == 0;
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
var labelText = Zip([Zip([Zip([Zip([Zip([Zip([Zip([Zip([Zip([clock]).map(function ($args) {
    return "It is: " + $args[0];
})]).map(function ($args) {
    return $args[0] + ", elapsed from entering page: ";
}), timeElapsed]).map(function ($args) {
    return $args[0] + $args[1];
})]).map(function ($args) {
    return $args[0] + " in ";
}), ticks]).map(function ($args) {
    return $args[0] + $args[1];
})]).map(function ($args) {
    return $args[0] + " real: ";
}), realTimeElapsed(start)]).map(function ($args) {
    return $args[0] + $args[1];
})]).map(function ($args) {
    return $args[0] + " is odd: ";
}), oddTime(clock)]).map(function ($args) {
    return $args[0] + $args[1];
});
/*-----*/

labelText.forEach(function (text) {
    document.getElementById("text").textContent = text;
});