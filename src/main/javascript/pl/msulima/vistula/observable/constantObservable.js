"use strict";

const ObservableImpl = require("./observable").ObservableImpl;

module.exports.constantObservable = function constantObservable(value) {
    const observable = new ObservableImpl();
    observable.rxPush(value);
    return observable;
};
