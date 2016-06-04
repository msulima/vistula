'use strict';

const ObservableImpl = require("./observable").ObservableImpl;
const util = require("./util");

function zipAndFlatten(observables) {
    return zip(observables).rxMap($arrays => {
        return [].concat.apply([], $arrays);
    });
}

function zip(observables) {
    if (observables.length == 0) {
        return util.constantObservable([]);
    }

    let results = observables.map(obs => {
        return {
            observable: obs,
            hasValue: false,
            lastValue: null,
            unsubscribe: null
        };
    });

    const observable = new ObservableImpl(() => {
        results.forEach(x => x.unsubscribe());
        results = null;
    });

    subscribeToAll(results, () => rxPush(observable, results));

    return observable;
}

function subscribeToAll(results, onChange) {
    results.forEach(state => {
        state.unsubscribe = state.observable.rxForEach(next => {
            state.hasValue = true;
            state.lastValue = next;

            onChange();
        });
    });
}

function rxPush(Source, results) {
    const allSet = results.every(result => {
        return result.hasValue;
    });

    if (allSet) {
        Source.rxPush(results.map(result => {
            return result.lastValue;
        }));
    }
}

module.exports = {
    zip: zip,
    zipAndFlatten: zipAndFlatten
};
