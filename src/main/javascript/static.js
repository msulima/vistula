"use strict";

const observableImpl = require("./observableImpl");

function staticValue(upstream, value) {
    return new StaticObservable(upstream, value, () => {
    });
}

function staticTransform(upstream, transform) {
    return new StaticObservable(upstream, null, transform);
}

const StaticObservable = function (upstream, value, transform, marker) {
    this.hasValue = true;
    this.lastValue = value;
    this.observers = [];

    this.transform = transform;

    this.isSubscribed = false;

    this.upstream = upstream;
    this.unsubscribeFromUpstream = null;
};

StaticObservable.prototype.rxForEach = rxForEach;
StaticObservable.prototype.rxForEachOnce = rxForEachOnce;
StaticObservable.prototype.rxMap = rxMap;
StaticObservable.prototype.rxFlatMap = rxFlatMap;

StaticObservable.prototype.unsubscribe = unsubscribe;

StaticObservable.prototype.subscribeToAll = subscribeToAll;
StaticObservable.prototype.unsubscribeFromAll = unsubscribeFromAll;

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
    this.unsubscribeFromUpstream = this.upstream.rxForEach(value => {
        this.transform(value);
    });
}

function unsubscribe(callback) {
    observableImpl.unsubscribe.call(this, callback);

    if (this.observers.length == 0) {
        this.unsubscribeFromAll();
    }
}

function unsubscribeFromAll() {
    this.isSubscribed = false;

    if (this.unsubscribeFromUpstream != null) {
        this.unsubscribeFromUpstream();
        this.unsubscribeFromUpstream = null;
    }
}

function rxMap(transformation) {
    return new observable.MapObservable(this, transformation);
}

function rxFlatMap(transformation) {
    return new observable.PointerObservable(this, transformation);
}

module.exports = {
    staticTransform: staticTransform,
    staticValue: staticValue
};
