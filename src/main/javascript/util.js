'use strict';

var ObservableImpl = require('./observable').ObservableImpl;

function zip(observables) {
    var observable = new ObservableImpl();

    var results = observables.map(function () {
        return {
            hasValue: false,
            lastValue: null
        };
    });

    observables.forEach(function (observable, i) {
        observable.forEach(function (next) {
            var state = results[i];
            state.hasValue = true;
            state.lastValue = next;
            onNext();
        });
    });

    function onNext() {
        var allSet = results.every(function (result) {
            return result.hasValue;
        });

        if (allSet) {
            observable.onNext(results.map(function (result) {
                return result.lastValue;
            }));
        }
    }

    return observable;
}

function constantObservable(value) {
    var observable = new ObservableImpl();
    observable.onNext(value);
    return observable;
}

function delayedObservable(value, delay) {
    var observable = new ObservableImpl();
    setTimeout(function () {
        observable.onNext(value);
    }, delay);
    return observable;
}

function aggregate(Initial, Source, createSource) {
    let $Obs = new ObservableImpl();
    Initial.forEach((initial, unsubscribe) => {
        unsubscribe();

        let $acc = initial;
        $Obs.onNext($acc);

        let $Following = Source.flatMap((source) => {
            return createSource($acc, source);
        });

        $Following.forEach((following) => {
            $acc = following;
            $Obs.onNext(following);
        });
    });

    return $Obs;
}

function distinctUntilChanged(Obs) {
    let proxy = new ObservableImpl();
    let hasValue = false;
    let lastValue = null;

    Obs.forEach(function ($arg) {
        let changed = !hasValue || (hasValue && lastValue != $arg);
        hasValue = true;

        if (changed) {
            lastValue = $arg;
            proxy.onNext($arg);
        }
    });

    return proxy;
}

function wrap(Obs) {
    return Obs();
}

function ifStatement(Condition, OnTrue, OnFalse) {
    return Condition.flatMap(function ($condition) {
        return $condition ? OnTrue : OnFalse;
    });
}

module.exports = {
    zip: zip,
    delayedObservable: delayedObservable,
    constantObservable: constantObservable,
    aggregate: aggregate,
    ifStatement: ifStatement,
    wrap: wrap,
    distinctUntilChanged: distinctUntilChanged
};
