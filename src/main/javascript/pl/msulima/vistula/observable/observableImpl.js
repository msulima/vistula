"use strict";

function rxForEach(callback) {
    this.observers.push(callback);

    if (this.hasValue) {
        callback(this.lastValue)
    }

    return this.unsubscribe.bind(this, callback);
}

function rxForEachOnce(callback) {
    const observer = value => {
        this.unsubscribe(observer);
        callback(value);
    };

    if (this.hasValue) {
        observer(this.lastValue);
    } else {
        this.observers.push(observer);
    }
}

function rxLastValue() {
    if (this.hasValue) {
        return this.lastValue;
    } else {
        throw new Error("Does not have value yet");
    }
}

function unsubscribe(callback) {
    if (this.upstreamUnsubscribe) {
        this.upstreamUnsubscribe();
    }
    this.observers = this.observers.filter(observer => {
        return observer != callback;
    });
}

function rxPush(value) {
    this.hasValue = true;
    this.lastValue = value;

    this.observers.forEach(obs => obs(value));
}

module.exports = {
    rxForEachOnce: rxForEachOnce,
    rxForEach: rxForEach,
    rxLastValue: rxLastValue,
    rxPush: rxPush,
    unsubscribe: unsubscribe
};
