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
