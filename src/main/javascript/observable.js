"use strict";

var PointerObservable = function (upstreamUnsubscribe) {
    this.hasValue = false;
    this.lastValue = null;
    this.pointsTo = null;
    this.unsubscribeFromPointsTo = null;
    this.observers = [];
    this.upstreamUnsubscribe = upstreamUnsubscribe;
};

var ObservableImpl = function (upstreamUnsubscribe) {
    this.hasValue = false;
    this.lastValue = null;
    this.observers = [];
    this.upstreamUnsubscribe = upstreamUnsubscribe;
};

ObservableImpl.prototype.rxForEach = PointerObservable.prototype.rxForEach = function (callback) {
    this.observers.push(callback);
    console.log(this.observers);

    return this.rxForEachOnce(callback);
};

ObservableImpl.prototype.rxForEachOnce = PointerObservable.prototype.rxForEachOnce = function (callback) {
    if (this.hasValue) {
        this._rxCall(callback);
    }

    return this.unsubscribe.bind(this, callback);
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

ObservableImpl.prototype._rxCall = PointerObservable.prototype._rxCall = function (callback) {
    callback(this.lastValue);
};

ObservableImpl.prototype.unsubscribe = PointerObservable.prototype.unsubscribe = function (callback) {
    if (this.upstreamUnsubscribe) {
        this.upstreamUnsubscribe();
    }
    this.observers = this.observers.filter(observer => {
        return observer != callback;
    });
};

ObservableImpl.prototype.rxMap = PointerObservable.prototype.rxMap = function (callback) {
    const observable = new ObservableImpl();

    this.rxForEach(value => {
        observable.rxPush(callback(value));
    });

    return observable;
};

ObservableImpl.prototype.rxFlatMap = PointerObservable.prototype.rxFlatMap = function (transformation) {
    const proxy = new PointerObservable();

    this.rxForEach(next => {
        proxy.rxPointTo(transformation(next));
    });

    return proxy;
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
