var timer = new vistula.ObservableImpl();
setInterval(timer.onNext.bind(timer), 1000);

var justOne = vistula.constantObservable(1);

var millisClock = timer.map(function () {
    return new Date().getTime();
});

var secondsClock = millisClock.map(function (time) {
    return Math.floor(time / 1000);
});

var delayedClock = millisClock.flatMap(function (time) {
    return vistula.delayedObservable(time, 500);
});

var constant = millisClock.flatMap(function (time) {
    return justOne;
});

var differential = vistula.aggregate(vistula.constantObservable(0), timer, ($acc, timer) => {
    console.log($acc, timer);
    let $Acc = vistula.constantObservable($acc);
    let Timer = vistula.constantObservable(timer);

    return vistula.zip([$Acc, Timer]).map(($zip) => {
        return $zip[0] + 1;
    });
});

function display(Obs, element) {
    Obs.forEach(function (value) {
        document.getElementById(element).textContent = JSON.stringify(value);
    });
}


var cursorX = new vistula.ObservableImpl();
var cursorY = new vistula.ObservableImpl();

document.addEventListener("mousemove", function (event) {
    cursorX.onNext(event.screenX);
    cursorY.onNext(event.screenY);
});

var cursor = vistula.constantObservable({
    x: cursorX,
    y: cursorY
});

var area = vistula.zip([cursor.flatMap(($arg) => {
    return $arg.x;
}), cursor.flatMap(($arg) => {
    return $arg.y;
})]).map(($args) => {
    return $args[0] * $args[1];
});

display(millisClock, "millisClock");
display(secondsClock, "secondsClock");
display(vistula.zip([millisClock, secondsClock]), "zip");
display(delayedClock, "delayedClock");
display(constant, "constant");
display(differential, "differential");
display(area, "area");


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
