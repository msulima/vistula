var _clock = Observable();
setInterval(_clock.onNext, 1000);
var clock = _clock.map(function () {
    return new Date().getTime();
});

var ticks_acc = 0;
var ticks = clock.map(function () {
    ticks_acc = ticks_acc + 1;
    return ticks_acc;
});

var start = Observable(new Date().getTime());

/*-------*/

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
var __labelText_6 = clock.map(function (clock) {
    return "It is: " + clock;
});
var __labelText_5 = __labelText_6.map(function (__labelText_6) {
    return __labelText_6 + ", elapsed from entering page: ";
});
var __labelText_4 = Zip([__labelText_5, timeElapsed]).map(function (__args) {
    var __labelText_5 = __args[0]; var timeElapsed = __args[1];
    return __labelText_5 + timeElapsed;
});
var __labelText_3 = __labelText_4.map(function (__labelText_4) {
    return __labelText_4 + " in ";
});
var __labelText_2 = Zip([__labelText_3, ticks]).map(function (__args) {
    var __labelText_3 = __args[0]; var ticks = __args[1];
    return __labelText_3 + ticks;
});
var __labelText_1 = __labelText_2.map(function (__labelText_2) {
    return __labelText_2 + " ";
});
var __labelText_14 = realTimeElapsed(start);
var labelText = Zip([__labelText_1, __labelText_14]).map(function (__args) {
    var __labelText_1 = __args[0]; var __labelText_14 = __args[1];
    return __labelText_1 + __labelText_14;
});
/*-----*/

labelText.forEach(function (text) {
    document.getElementById("text").textContent = text;
});