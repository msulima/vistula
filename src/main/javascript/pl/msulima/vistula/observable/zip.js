"use strict";

const observable = require("./observable");
const observableImpl = require("./observableImpl");
const util = require("./util");
const constantObservable = require("./constantObservable");

function zipAndFlatten(observables) {
    return zip(observables).rxMap($arrays => {
        return [].concat.apply([], $arrays);
    });
}

function zip(observables) {
    if (observables.length == 0) {
        return constantObservable.constantObservable([]);
    }

    return new ZipObservable(observables);
}

const ZipObservable = function (observables) {
    this.hasValue = false;
    this.lastValue = null;
    this.observers = [];

    this.isSubscribed = false;

    this.results = observables.map(obs => ({
        observable: obs,
        hasValue: false,
        lastValue: null,
        unsubscribe: null
    }));
};

ZipObservable.prototype.rxForEach = rxForEach;
ZipObservable.prototype.rxForEachOnce = rxForEachOnce;
ZipObservable.prototype.rxMap = rxMap;
ZipObservable.prototype.rxFlatMap = rxFlatMap;

ZipObservable.prototype._rxPush = observableImpl.rxPush;
ZipObservable.prototype.unsubscribe = unsubscribe;

ZipObservable.prototype.subscribeToAll = subscribeToAll;
ZipObservable.prototype.unsubscribeFromAll = unsubscribeFromAll;
ZipObservable.prototype.onChange = onChange;

function rxForEach(callback) {
    if (!this.isSubscribed) {
        this.subscribeToAll();
    }

    return observableImpl.rxForEach.call(this, callback);
}

function rxForEachOnce(callback) {
    if (!this.isSubscribed) {
        this.subscribeToAll();
    }

    return observableImpl.rxForEachOnce.call(this, callback);
}

function subscribeToAll() {
    this.isSubscribed = true;
    this.results.forEach(state => {
        state.unsubscribe = state.observable.rxForEach(next => {
            state.hasValue = true;
            state.lastValue = next;

            this.onChange();
        });
    });
}

function onChange() {
    const allSet = this.results.every(result => {
        return result.hasValue;
    });

    if (allSet) {
        this._rxPush(this.results.map(result => {
            return result.lastValue;
        }));
    }
}


function unsubscribe(callback) {
    observableImpl.unsubscribe.call(this, callback);

    if (this.observers.length == 0) {
        this.unsubscribeFromAll();
    }
}

function unsubscribeFromAll() {
    this.isSubscribed = false;

    this.hasValue = false;
    this.lastValue = false;

    this.results.forEach(state => {
        state.unsubscribe();
        state.hasValue = false;
        state.lastValue = null;
    });
}

function rxMap(transformation) {
    return new observable.MapObservable(this, transformation);
}

function rxFlatMap(transformation) {
    return new observable.PointerObservable(this, transformation);
}

module.exports = {
    zip: zip,
    zipAndFlatten: zipAndFlatten
};
