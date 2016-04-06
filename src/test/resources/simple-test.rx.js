var timer = new ObservableImpl();
setInterval(timer.onNext.bind(timer), 1000);

var justOne = ConstantObservable(1);

var millisClock = timer.map(function () {
    return new Date().getTime();
});

var secondsClock = millisClock.map(function (time) {
    return Math.floor(time / 1000);
});

var delayedClock = millisClock.flatMap(function (time) {
    return DelayedObservable(time, 500);
});

var constant = millisClock.flatMap(function (time) {
    return justOne;
});

millisClock.forEach(function (time) {
    document.getElementById("millisClock").textContent = JSON.stringify(time);
});

secondsClock.forEach(function (time) {
    document.getElementById("secondsClock").textContent = JSON.stringify(time);
});

Zip([millisClock, secondsClock]).forEach(function (zip) {
    document.getElementById("zip").textContent = JSON.stringify(zip);
});

delayedClock.forEach(function (time) {
    document.getElementById("delayedClock").textContent = JSON.stringify(time);
});

constant.forEach(function (time) {
    document.getElementById("constant").textContent = JSON.stringify(time);
});

//
//var third = secondClock.flatMap(function (time) {
//    return justOne.map(function (x) {
//        return x * time;
//    });
//});
//
//third.forEach(function (time) {
//    console.log("third", time);
//});
//
//Zip([secondClock, third]).forEach(function (zip) {
//    document.getElementById("zip").textContent = JSON.stringify(zip);
//});
