"use strict";

const common = require("./common");

const PointerObservable = function (upstreamUnsubscribe) {
    this.hasValue = false;
    this.lastValue = null;
    this.pointsTo = null;
    this.unsubscribeFromPointsTo = null;
    this.observers = [];
    this.upstreamUnsubscribe = upstreamUnsubscribe;
};

const MapObservable = function (upstream, transformation) {
    this.upstream = upstream;
    this.transformation = transformation;
};

const ObservableImpl = function (upstreamUnsubscribe) {
    this.hasValue = false;
    this.lastValue = null;
    this.observers = [];
    this.upstreamUnsubscribe = upstreamUnsubscribe;
};

ObservableImpl.prototype.rxForEach = PointerObservable.prototype.rxForEach = common.rxForEach;

ObservableImpl.prototype.rxForEachOnce = PointerObservable.prototype.rxForEachOnce = common.rxForEachOnce;

MapObservable.prototype.rxForEach = function (callback) {
    return this.upstream.rxForEach(value => {
        callback(this.transformation(value));
    });
};

MapObservable.prototype.rxForEachOnce = function (callback) {
    return this.upstream.rxForEachOnce(value => {
        callback(this.transformation(value));
    });
};

MapObservable.prototype.rxMap = function (transformation) {
    return this.upstream.rxMap(value => {
        return transformation(this.transformation(value));
    });
};

MapObservable.prototype.rxFlatMap = function (transformation) {
    return this.upstream.rxFlatMap(value => {
        return transformation(this.transformation(value));
    });
};

ObservableImpl.prototype.rxPush = function (value) {
    this.hasValue = true;
    this.lastValue = value;

    this.observers.forEach(this._rxCall, this);
};

PointerObservable.prototype.rxPush = function (value) {
    if (this.pointsTo == null) {
        throw "Cannot push to unset pointer";
    }

    this.pointsTo.rxPush(value);
};

ObservableImpl.prototype._rxCall = PointerObservable.prototype._rxCall = MapObservable.prototype._rxCall = common._rxCall;

ObservableImpl.prototype.unsubscribe = PointerObservable.prototype.unsubscribe = common.unsubscribe;

ObservableImpl.prototype.rxMap = PointerObservable.prototype.rxMap = function (transformation) {
    return new MapObservable(this, transformation);
};

ObservableImpl.prototype.rxFlatMap = PointerObservable.prototype.rxFlatMap = function (transformation) {
    const Proxy = new PointerObservable();

    this.rxForEach(next => {
        Proxy.rxPointTo(transformation(next));
    });

    return Proxy;
};

PointerObservable.prototype.rxPointTo = function (observable) {
    if (observable == this.pointsTo) {
        return;
    }

    if (this.unsubscribeFromPointsTo != null) {
        this.unsubscribeFromPointsTo();
    }

    this.pointsTo = observable;
    this.unsubscribeFromPointsTo = observable.rxForEach(value => {
        if (observable == this.pointsTo) {
            ObservableImpl.prototype.rxPush.apply(this, [value]);
        }
    });
};

module.exports.PointerObservable = PointerObservable;
module.exports.ObservableImpl = ObservableImpl;
