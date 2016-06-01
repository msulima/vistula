"use strict";

var ObservableImpl = require('./observable').ObservableImpl;
var PointerObservable = require('./observable').PointerObservable;
var util = require("./util");

function ifChangedArrays(Condition, FragmentsTrue, FragmentsFalse) {
    return distinctUntilChanged(Condition).rxFlatMap(function (condition) {
        const Fragments = condition ? FragmentsTrue : FragmentsFalse;

        return util.zip(Fragments).rxMap(function ($arrays) {
            return [].concat.apply([], $arrays);
        });
    });
}

function distinctUntilChanged(Obs) {
    const proxy = new ObservableImpl();
    let hasValue = false;
    let lastValue = null;

    Obs.rxForEach($arg => {
        let changed = !hasValue || (hasValue && lastValue != $arg);
        hasValue = true;

        if (changed) {
            lastValue = $arg;
            proxy.rxPush($arg);
        }
    });

    return proxy;
}

module.exports = {
    ifChangedArrays: ifChangedArrays
};
