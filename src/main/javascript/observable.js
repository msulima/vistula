'use strict';

var ObservableImpl = function () {
    this.hasValue = false;
    this.lastValue = null;
    this.observers = [];
};

ObservableImpl.prototype.rxForEach = function (callback) {
    this.observers.push(callback);

    if (this.hasValue) {
        this._rxCall(callback);
    }
};

ObservableImpl.prototype.rxPush = function (value) {
    this.hasValue = true;
    this.lastValue = value;

    this.observers.map(this._rxCall, this);
};

ObservableImpl.prototype._rxCall = function (callback) {
    callback(this.lastValue, this.unsubscribe.bind(this, callback));
};

ObservableImpl.prototype.rxSet = function (value) {
    if (this.constant) {
        this.rxPush(value);
    } else {
        throw "Cannot set value";
    }
};

ObservableImpl.prototype.unsubscribe = function (callback) {
    this.observers = this.observers.filter(function (observer) {
        return observer != callback;
    });
};

ObservableImpl.prototype.rxMap = function (callback) {
    var observable = new ObservableImpl();

    this.rxForEach(function (value) {
        observable.rxPush(callback(value));
    });

    return observable;
};

ObservableImpl.prototype.rxFlatMap = function (callback) {
    // TODO hacky as fuck
    if (this.constant) {
        var nestedObservable = callback(this.lastValue);
        if (nestedObservable.constant) {
            return nestedObservable;
        }
    }
    var proxy = new ObservableImpl();
    var currentObservable = null;

    this.rxForEach(function (next) {
        var nestedObservable = callback(next);
        currentObservable = nestedObservable;

        nestedObservable.rxForEach(function (value, unsubscribeCallback) {
            if (nestedObservable == currentObservable) {
                proxy.rxPush(value);
            } else {
                unsubscribeCallback();
            }
        });
    });

    return proxy;
};

module.exports.ObservableImpl = ObservableImpl;
