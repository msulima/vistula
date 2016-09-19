"use strict";

const timer = vistula.constantObservable(0);
setInterval(timer.rxPush.bind(timer), 1000);

const clock = timer.rxMap(function (ignored) {
    return new Date();
});

module.exports = {
    clock: clock
};
