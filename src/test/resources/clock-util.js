var timer = new vistula.ObservableImpl();
setInterval(timer.rxPush.bind(timer), 1000);

var clock = timer.rxMap(function () {
    return new Date().getTime();
});

var cursorX = vistula.constantObservable(0);
var cursorY = vistula.constantObservable(0);

document.addEventListener("mousemove", function (event) {
    cursorX.rxPush(event.clientX);
    cursorY.rxPush(event.clientY);
});

var cursor = vistula.constantObservable({
    x: cursorX,
    y: cursorY
});
