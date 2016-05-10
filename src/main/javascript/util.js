'use strict';

var ObservableImpl = require('./observable').ObservableImpl;

function zipAndFlatten(observables) {
    return zip(observables).rxMap(function ($arrays) {
        return [].concat.apply([], $arrays);
    });
}

function zip(observables) {
    var observable = new ObservableImpl();

    if (observables.length == 0) {
        return constantObservable([]);
    }

    var results = observables.map(function () {
        return {
            hasValue: false,
            lastValue: null
        };
    });

    observables.forEach(function (observable, i) {
        observable.rxForEach(function (next) {
            var state = results[i];
            state.hasValue = true;
            state.lastValue = next;
            rxPush();
        });
    });

    function rxPush() {
        var allSet = results.every(function (result) {
            return result.hasValue;
        });

        if (allSet) {
            observable.rxPush(results.map(function (result) {
                return result.lastValue;
            }));
        }
    }

    return observable;
}

function constantObservable(value) {
    var observable = new ObservableImpl();
    observable.constant = true;
    observable.rxPush(value);
    return observable;
}

function delayedObservable(value, delay) {
    var observable = new ObservableImpl();
    setTimeout(function () {
        observable.rxPush(value);
    }, delay);
    return observable;
}

function aggregate(Initial, Source, createSource) {
    let $Obs = new ObservableImpl();
    Initial.rxForEach((initial, unsubscribe) => {
        unsubscribe();

        let $acc = initial;
        $Obs.rxPush($acc);

        let $Following = Source.rxFlatMap((source) => {
            return createSource($acc, source);
        });

        $Following.rxForEach((following) => {
            $acc = following;
            $Obs.rxPush(following);
        });
    });

    return $Obs;
}

function distinctUntilChanged(Obs) {
    let proxy = new ObservableImpl();
    let hasValue = false;
    let lastValue = null;

    Obs.rxForEach(function ($arg) {
        let changed = !hasValue || (hasValue && lastValue != $arg);
        hasValue = true;

        if (changed) {
            lastValue = $arg;
            proxy.rxPush($arg);
        }
    });

    return proxy;
}

function wrap(Obs) {
    return Obs();
}

function ifStatement(Condition, OnTrue, OnFalse) {
    return Condition.rxFlatMap(function ($condition) {
        return $condition ? OnTrue : OnFalse;
    });
}

function toObservable(value) {
    if (Array.isArray(value)) {
        return arrayToObservable(value);
    } else if (typeof value == "object") {
        return objectToObservable(value);
    } else {
        return constantObservable(value)
    }
}

function arrayToObservable(obj) {
    return constantObservable(obj.map(function (x) {
        return toObservable(x);
    }));
}

function objectToObservable(obj) {
    let target = {};

    Object.keys(obj).forEach(function (key) {
        target[key] = toObservable(obj[key]);
    });

    return constantObservable(target);
}

module.exports = {
    aggregate: aggregate,
    constantObservable: constantObservable,
    delayedObservable: delayedObservable,
    distinctUntilChanged: distinctUntilChanged,
    ifStatement: ifStatement,
    toObservable: toObservable,
    wrap: wrap,
    zip: zip,
    zipAndFlatten: zipAndFlatten
};
