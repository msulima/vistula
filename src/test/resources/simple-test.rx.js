var timer = new ObservableImpl();
setInterval(timer.onNext.bind(timer), 1000);

var justOne = new ObservableImpl(1000);

var millisClock = timer.map(function () {
    return new Date().getTime();
});

var secondsClock = millisClock.map(function (time) {
    return Math.floor(time / 1000);
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
