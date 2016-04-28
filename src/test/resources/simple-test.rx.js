var timer = new vistula.ObservableImpl();
setInterval(timer.onNext.bind(timer), 1000);

var justOne = vistula.ConstantObservable(1);

var millisClock = timer.map(function () {
    return new Date().getTime();
});

var secondsClock = millisClock.map(function (time) {
    return Math.floor(time / 1000);
});

var delayedClock = millisClock.flatMap(function (time) {
    return vistula.DelayedObservable(time, 500);
});

var constant = millisClock.flatMap(function (time) {
    return justOne;
});

var differential = vistula.aggregate(vistula.ConstantObservable(0), timer, ($acc, timer) => {
    console.log($acc, timer);
    let $Acc = vistula.ConstantObservable($acc);
    let Timer = vistula.ConstantObservable(timer);

    return vistula.Zip([$Acc, Timer]).map(($zip) => {
        return $zip[0] + 1;
    });
});

function display(Obs, element) {
    Obs.forEach(function (value) {
        document.getElementById(element).textContent = JSON.stringify(value);
    });
}

display(millisClock, "millisClock");
display(secondsClock, "secondsClock");
display(vistula.Zip([millisClock, secondsClock]), "zip");
display(delayedClock, "delayedClock");
display(constant, "constant");
display(differential, "differential");


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
