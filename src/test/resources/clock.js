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
    var __Temp_1 = clock.map(function (clock) {
        return clock % 2;
    });
    var __ifCondition = __Temp_1.map(function (__Temp_1) {
        return __Temp_1 == 0;
    });
    return __ifCondition.flatMap(function (__ifCondition) {
        if (__ifCondition) {
            return clock;
        } else {
            return ConstantObservable("odd");
        }
    });
}
function realTimeElapsed(elapsed) {
    return Zip([clock, elapsed]).map(function (__args) {
        var clock = __args[0]; var elapsed = __args[1];
        return clock - elapsed;
    });
}
var timeElapsed = Zip([clock, start]).map(function (__args) {
    var clock = __args[0]; var start = __args[1];
    return clock - start;
});
var __labelText_8 = clock.map(function (clock) {
    return "It is: " + clock;
});
var __labelText_7 = __labelText_8.map(function (__labelText_8) {
    return __labelText_8 + ", elapsed from entering page: ";
});
var __labelText_6 = Zip([__labelText_7, timeElapsed]).map(function (__args) {
    var __labelText_7 = __args[0]; var timeElapsed = __args[1];
    return __labelText_7 + timeElapsed;
});
var __labelText_5 = __labelText_6.map(function (__labelText_6) {
    return __labelText_6 + " in ";
});
var __labelText_4 = Zip([__labelText_5, ticks]).map(function (__args) {
    var __labelText_5 = __args[0]; var ticks = __args[1];
    return __labelText_5 + ticks;
});
var __labelText_3 = __labelText_4.map(function (__labelText_4) {
    return __labelText_4 + " real: ";
});
var __labelText_16 = realTimeElapsed(start);
var __labelText_2 = Zip([__labelText_3, __labelText_16]).map(function (__args) {
    var __labelText_3 = __args[0]; var __labelText_16 = __args[1];
    return __labelText_3 + __labelText_16;
});
var __labelText_1 = __labelText_2.map(function (__labelText_2) {
    return __labelText_2 + " is odd: ";
});
var __labelText_19 = oddTime(clock);
var labelText = Zip([__labelText_1, __labelText_19]).map(function (__args) {
    var __labelText_1 = __args[0]; var __labelText_19 = __args[1];
    return __labelText_1 + __labelText_19;
});
/*-----*/

labelText.forEach(function (text) {
    document.getElementById("text").textContent = text;
});