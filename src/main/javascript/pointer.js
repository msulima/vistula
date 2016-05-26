"use strict";

var ObservableImpl = require('./observable').ObservableImpl;

var PointerObservable = function () {
    this.hasValue = false;
    this.pointsTo = null;
    this.unsubscribeFromPointsTo = null;
    this.observers = [];
};

PointerObservable.prototype.rxPointTo = function (observable) {
    if (observable == this.pointsTo) {
        return;
    }

    if (this.unsubscribeFromPointsTo != null) {
        this.unsubscribeFromPointsTo();
    }
    this.hasValue = true;
    this.pointsTo = observable;

    observable.rxForEach((value, unsubscribe) => {
        if (observable == this.pointsTo) {
            this.unsubscribeFromPointsTo = unsubscribe;
            this.observers.forEach(observer => {
                observer(value, this.unsubscribe.bind(this));
            });
        } else {
            unsubscribe();
        }
    });
};

PointerObservable.prototype.rxForEach = function (callback) {
    this.observers.push(callback);

    if (this.hasValue) {
        this._rxCall(callback);
    }
};

PointerObservable.prototype.rxPush = function (value) {
    this.pointsTo.rxPush(value);
};

PointerObservable.prototype._rxCall = function (callback) {
    const localPointsTo = this.pointsTo;

    localPointsTo.rxForEach((value, unsubscribe) => {
        unsubscribe();
        if (localPointsTo == this.pointsTo) {
            callback(value, this.unsubscribe.bind(this, callback));
        }
    });
};

PointerObservable.prototype.unsubscribe = function (callback) {
    this.observers = this.observers.filter(observer => {
        return observer != callback;
    });
};

PointerObservable.prototype.rxMap = ObservableImpl.prototype.rxMap;

PointerObservable.prototype.rxFlatMap = ObservableImpl.prototype.rxFlatMap;

module.exports.PointerObservable = PointerObservable;
