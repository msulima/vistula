var clock = new ObservableImpl();
setInterval(clock.onNext.bind(clock), 1000);

var justOne = new ObservableImpl(1000);

var secondClock = clock.map(function () {
    return new Date().getTime() / 1000;
});

secondClock.forEach(function (time) {
    document.getElementById("secondClock").textContent = JSON.stringify(time);
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
