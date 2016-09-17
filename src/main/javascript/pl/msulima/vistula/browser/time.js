"use strict";

const timer = new vistula.ObservableImpl();
setInterval(timer.rxPush.bind(timer), 1000);

const clock = timer.rxMap(function () {
    return new Date().getTime();
});

module.exports = {
    clock: clock
};
