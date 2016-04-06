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

var timeElapsed = Zip([clock, start]).map(function (args) {
    return args[0] - args[1];
});

timeElapsed.forEach(function (text) {
    document.getElementById("debug").textContent = text;
});

function serverTime() {
    var obs = new ObservableImpl();

    var request = new XMLHttpRequest();
    request.onreadystatechange = function () {
        var DONE = this.DONE || 4;
        if (this.readyState === DONE) {
            console.log(this.readyState);
        }
    };
    request.open('GET', 'http://localhost:8080/time', true);
    request.send(null);

    return obs;
}

function compareWithServerTime(currentTime) {
    console.log((currentTime / 1000) % 3);
    if (Math.floor(currentTime / 1000) % 3 == 0) {
        return serverTime();
    } else {
        return ConstantObservable("unknown");
    }
}

function oddTime(CurrentTime) {
    var X = CurrentTime.map(function (currentTime) {
        return currentTime % 2 == 0;
    });

    return X.flatMap(function (x) {
        if (x) {
            return CurrentTime;
        } else {
            return ConstantObservable("unknown");
        }
    });
}

function realTimeElapsed(elapsed) {
    return Zip([clock, elapsed]).map(function (__args) {
        var clock = __args[0];
        var elapsed = __args[1];
        return clock - elapsed;
    });
}

var _realTimeElapsed_start = Zip([start]).flatMap(function (args) {
    return realTimeElapsed(args[0]);
});

var labelText = Zip([clock, timeElapsed, ticks, start, _realTimeElapsed_start]).map(function (args) {
    return "It is: " + args[0] + ", elapsed from entering page: " + args[1] + " real time " + args[4] + " in " + args[2] + " ticks";
});


labelText.forEach(function (text) {
    document.getElementById("text").textContent = text;
});

clock.flatMap(compareWithServerTime).forEach(function (text) {
    document.getElementById("currentTimeText").textContent = text;
});

function y(z) {
    Observable(z);
}

// X + y(Z)

// W = y(Z)
// X + W

var X, Z;

var W = Z.flatMap(function (z) {
    return y(z);
});

Zip([X, W]).map(function (args) {
    var x = args[0];
    var w = args[1];
    return x + args[1];
});
