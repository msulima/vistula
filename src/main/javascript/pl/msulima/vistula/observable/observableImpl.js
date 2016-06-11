"use strict";

const rxForEach = function (callback) {
    this.observers.push(callback);

    if (this.hasValue) {
        callback(this.lastValue)
    }

    return this.unsubscribe.bind(this, callback);
};

const rxForEachOnce = function (callback) {
    const observer = value => {
        this.unsubscribe(observer);
        callback(value);
    };

    if (this.hasValue) {
        observer(this.lastValue);
    } else {
        this.observers.push(observer);
    }
};

const unsubscribe = function (callback) {
    if (this.upstreamUnsubscribe) {
        this.upstreamUnsubscribe();
    }
    this.observers = this.observers.filter(observer => {
        return observer != callback;
    });
};

function rxPush(value) {
    this.hasValue = true;
    this.lastValue = value;

    this.observers.forEach(obs => obs(value));
}

module.exports = {
    rxForEachOnce: rxForEachOnce,
    rxForEach: rxForEach,
    rxPush: rxPush,
    unsubscribe: unsubscribe
};
