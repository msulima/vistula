'use strict';

const ObservableImpl = require('./observable').ObservableImpl;

function zipAndFlatten(observables) {
    return zip(observables).rxMap($arrays => {
        return [].concat.apply([], $arrays);
    });
}

function zip(observables) {
    if (observables.length == 0) {
        return constantObservable([]);
    }

    const results = observables.map(() => {
        return {
            hasValue: false,
            lastValue: null,
            unsubscribe: null
        };
    });

    const observable = new ObservableImpl(() => results.forEach(x => x.unsubscribe()));

    function rxPush() {
        const allSet = results.every(result => {
            return result.hasValue;
        });

        if (allSet) {
            observable.rxPush(results.map(result => {
                return result.lastValue;
            }));
        }
    }

    observables.forEach((observable, i) => {
        const state = results[i];
        state.unsubscribe = observable.rxForEach(next => {
            state.hasValue = true;
            state.lastValue = next;
            rxPush();
        });
    });

    return observable;
}

function constantObservable(value) {
    const observable = new ObservableImpl();
    observable.rxPush(value);
    return observable;
}

function delayedObservable(value, delay) {
    const observable = new ObservableImpl();
    setTimeout(() => {
        observable.rxPush(value);
    }, delay);
    return observable;
}

function aggregate(Initial, Source, createSource) {
    const $Obs = new ObservableImpl();
    Initial.rxForEachOnce(initial => {
        let $acc = initial;
        $Obs.rxPush($acc);

        const $Following = Source.rxFlatMap(source => {
            return createSource($acc, source);
        });

        $Following.rxForEach(following => {
            $acc = following;
            $Obs.rxPush(following);
        });
    });

    return $Obs;
}

function wrap(Obs) {
    return Obs();
}

function ifStatement(Condition, OnTrue, OnFalse) {
    return Condition.rxFlatMap($condition => {
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
    return constantObservable(obj.map(toObservable));
}

function objectToObservable(obj) {
    const target = {};

    Object.keys(obj).forEach(function (key) {
        target[key] = toObservable(obj[key]);
    });

    return constantObservable(target);
}

module.exports = {
    aggregate: aggregate,
    constantObservable: constantObservable,
    delayedObservable: delayedObservable,
    ifStatement: ifStatement,
    toObservable: toObservable,
    wrap: wrap,
    zip: zip,
    zipAndFlatten: zipAndFlatten
};
