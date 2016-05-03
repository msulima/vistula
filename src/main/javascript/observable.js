'use strict';

var ObservableImpl = function () {
    this.hasValue = false;
    this.lastValue = null;
    this.observers = [];
};

ObservableImpl.prototype.rxForEach = function (callback) {
    this.observers.push(callback);

    if (this.hasValue) {
        callback(this.lastValue, this.unsubscribe.bind(this, callback));
    }
};

ObservableImpl.prototype.rxPush = function (value) {
    this.hasValue = true;
    this.lastValue = value;
    var _this = this;

    this.observers.map(function (callback) {
        callback(value, _this.unsubscribe.bind(_this, callback));
    });
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
