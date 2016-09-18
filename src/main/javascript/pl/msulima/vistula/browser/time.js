"use strict";

const timer = new vistula.ObservableImpl();
setInterval(timer.rxPush.bind(timer), 1000);

const clock = timer.rxMap(function () {
    return new Date();
});

module.exports = {
    clock: clock
};
