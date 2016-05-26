"use strict";

var ObservableImpl = function () {
    this.hasValue = false;
    this.lastValue = null;
    this.observers = [];
    this.isProxy = false;
    this.proxyFor = null;
};

ObservableImpl.prototype.rxForEach = function (callback) {
    this.observers.push(callback);

    if (this.hasValue) {
        this._rxCall(callback);
    }
};

ObservableImpl.prototype.rxPush = function (value) {
    this._rxPush(value);

    if (this.isProxy) {
        this.proxyFor.rxPush(value);
    }
};

ObservableImpl.prototype._rxPush = function (value) {
    this.hasValue = true;
    this.lastValue = value;

    this.observers.forEach(this._rxCall, this);
};

ObservableImpl.prototype._rxCall = function (callback) {
    callback(this.lastValue, this.unsubscribe.bind(this, callback));
};

ObservableImpl.prototype.unsubscribe = function (callback) {
    this.observers = this.observers.filter(observer => {
        return observer != callback;
    });
};

ObservableImpl.prototype.rxMap = function (callback) {
    var observable = new ObservableImpl();

    this.rxForEach(value => {
        observable.rxPush(callback(value));
    });

    return observable;
};

ObservableImpl.prototype.rxFlatMap = function (transformation) {
    let proxy = new ObservableImpl();
    proxy.isProxy = true;

    let previousObservable = null;
    let unsubscribeFromPreviousObservable = null;

    this.rxForEach(next => {
        let nestedObservable = transformation(next);
        if (nestedObservable.isProxy) {
            nestedObservable = nestedObservable.proxyFor;
        }
        proxy.proxyFor = nestedObservable;

        if (previousObservable != null && unsubscribeFromPreviousObservable != null) {
            unsubscribeFromPreviousObservable();
            unsubscribeFromPreviousObservable = null;
        }
        previousObservable = nestedObservable;

        nestedObservable.rxForEach((value, unsubscribeCallback) => {
            unsubscribeFromPreviousObservable = unsubscribeCallback;
            proxy._rxPush(value);
        });
    });

    return proxy;
};

module.exports.ObservableImpl = ObservableImpl;
