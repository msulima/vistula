"use strict";

const ObservableImpl = require("./observable").ObservableImpl;
const zip = require("./zip");
const constantObservable = require("./constantObservable");

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
        return constantObservable.constantObservable(value)
    }
}

function arrayToObservable(obj) {
    return constantObservable.constantObservable(obj.map(toObservable));
}

function objectToObservable(obj) {
    const target = {};

    Object.keys(obj).forEach(function (key) {
        target[key] = toObservable(obj[key]);
    });

    return constantObservable.constantObservable(target);
}

function fromObservable(Value) {
    return Value.rxFlatMap(value => {
        if (Array.isArray(value)) {
            return arrayFromObservable(value);
        } else if (typeof value == "object") {
            return objectFromObservable(value);
        } else {
            return Value;
        }
    });
}

function arrayFromObservable(values) {
    return zip.zip(values.map(fromObservable));
}

function objectFromObservable(value) {
    const keys = Object.keys(value);

    return zip.zip(keys.map(key => {
        return value[key];
    })).rxMap(values => {
        const obj = {};
        keys.forEach((key, index) => {
            obj[key] = values[index];
        });
        return obj;
    });
}

module.exports = {
    aggregate: aggregate,
    delayedObservable: delayedObservable,
    ifStatement: ifStatement,
    toObservable: toObservable,
    fromObservable: fromObservable,
    wrap: wrap
};
