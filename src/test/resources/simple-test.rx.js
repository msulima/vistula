var clock = Observable();
setInterval(clock.onNext, 1000);

var justOne = Observable(1000);

var secondClock = clock.map(function () {
    return new Date().getTime() / 1000;
});

var third = secondClock.flatMap(function (time) {
    return justOne.map(function (x) {
        return x * time;
    });
});

secondClock.forEach(function (time) {
    console.log("second", time);
});

third.forEach(function (time) {
    console.log("third", time);
});

Zip([secondClock, third]).forEach(function (zip) {
    console.log("zip", zip);
});
