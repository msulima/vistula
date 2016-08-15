"use strict";

const ObservableImpl = require("./observable").ObservableImpl;
const zip = require("./zip");

function ifChangedArrays(Condition, FragmentsTrue, FragmentsFalse) {
    return distinctUntilChanged(Condition).rxFlatMap(function (condition) {
        const Fragments = condition ? FragmentsTrue : FragmentsFalse;

        return zip.zip(Fragments).rxMap(function ($arrays) {
            return [].concat.apply([], $arrays);
        });
    });
}

function distinctUntilChanged(Obs) {
    const proxy = new ObservableImpl(() => {
        // FIXME what if unsubscribe, then subscribe?
    }, () => {
        unsubscribe();
    });
    let hasValue = false;
    let lastValue = null;

    const unsubscribe = Obs.rxForEach($arg => {
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
