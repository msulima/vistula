var timer = new vistula.ObservableImpl();
setInterval(timer.rxPush.bind(timer), 1000);

var clock = timer.rxMap(function () {
    return new Date().getTime();
});

var cursorX = new vistula.ObservableImpl();
var cursorY = new vistula.ObservableImpl();

document.addEventListener("mousemove", function (event) {
    cursorX.rxPush(event.screenX);
    cursorY.rxPush(event.screenY);
});

var cursor = vistula.constantObservable({
    x: cursorX,
    y: cursorY
});
