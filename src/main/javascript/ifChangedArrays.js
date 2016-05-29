"use strict";

var ObservableImpl = require('./observable').ObservableImpl;
var PointerObservable = require('./observable').PointerObservable;
var util = require("./util");

function ifChangedArrays(Condition, FragmentsTrue, FragmentsFalse) {
    const Pointer = new PointerObservable();

    distinctUntilChanged(Condition).rxForEach(function (condition) {
        let Fragments = condition ? FragmentsTrue : FragmentsFalse;

        Pointer.rxPointTo(util.zip(Fragments).rxMap(function ($arrays) {
            return [].concat.apply([], $arrays);
        }));
    });

    return Pointer;
}

function distinctUntilChanged(Obs) {
    let proxy = new ObservableImpl();
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
