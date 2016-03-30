var _clock = Observable();
setInterval(_clock.onNext, 100);
var clock = _clock.map(function () {
    return new Date().getTime();
});

var ticks_acc = 0;
var ticks = clock.map(function (time) {
    ticks_acc = ticks_acc + 1;
    return ticks_acc;
});

var start = Observable(new Date().getTime());

var timeElapsed = Zip([clock, start]).map(function (args) {
    return args[0] - args[1];
});

timeElapsed.forEach(function (text) {
    document.getElementById("debug").textContent = text;
});

function realTimeElapsed(elapsed) {
    return clock.map(function (time) {
        console.log("hi!", time, elapsed);
        return time - elapsed;
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

