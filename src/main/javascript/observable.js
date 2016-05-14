"use strict";

var ObservableImpl = function () {
    this.hasValue = false;
    this.lastValue = null;
    this.observers = [];
    this.isProxy = false;
    this.proxyFor = null;
    this.isConstant = false;
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

    this.observers.map(this._rxCall, this);
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

    let previousObservable = null;
    let unsubscribeFromPreviousObservable = null;

    this.rxForEach(next => {
        let nestedObservable = transformation(next);
        if (nestedObservable.isProxy) {
            nestedObservable = nestedObservable.proxyFor;
        }

        if (previousObservable != null && unsubscribeFromPreviousObservable != null) {
            unsubscribeFromPreviousObservable();
            unsubscribeFromPreviousObservable = null;
        }
        previousObservable = nestedObservable;

        proxy.isProxy = nestedObservable.isConstant;
        if (nestedObservable.isConstant) {
            proxy.proxyFor = nestedObservable;
        } else {
            proxy.proxyFor = null;
        }

        nestedObservable.rxForEach((value, unsubscribeCallback) => {
            unsubscribeFromPreviousObservable = unsubscribeCallback;
            proxy._rxPush(value);
        });
    });

    return proxy;
};

module.exports.ObservableImpl = ObservableImpl;
