"use strict";

const observableImpl = require("./observableImpl");

function rxForEach(callback) {
    if (!this.isSubscribed) {
        this.isSubscribed = true;
        this.unsubscribeFromUpstream = this.upstream.rxForEach(next => {
            this.rxPointTo(this.transformation(next));
        });
    }

    return observableImpl.rxForEach.call(this, callback);
}

function rxForEachOnce(callback) {
    if (!this.isSubscribed) {
        this.isSubscribed = true;
        this.unsubscribeFromUpstream = this.upstream.rxForEach(next => {
            this.rxPointTo(this.transformation(next));
        });
    }

    return observableImpl.rxForEachOnce.call(this, callback);
}

function rxPush(value) {
    if (this.pointsTo == null) {
        this.rxForEachOnce(() => { // FIXME
        });

        if (this.pointsTo == null) {
            throw "Cannot push to unset pointer";
        }
    }

    this.pointsTo.rxPush(value);
}

function unsubscribe(callback) {
    observableImpl.unsubscribe.call(this, callback);

    if (this.observers.length == 0) {
        this.isSubscribed = false;
        if (this.unsubscribeFromUpstream != null) {
            this.unsubscribeFromUpstream();
            this.unsubscribeFromUpstream = null;
        }

        if (this.unsubscribeFromPointsTo != null) {
            this.unsubscribeFromPointsTo();
            this.unsubscribeFromPointsTo = null;
        }

        this.hasValue = false;
        this.lastValue = null;
    }
}

function rxPointTo(observable) {
    if (observable == this.pointsTo && this.hasValue) {
        return;
    }

    if (this.unsubscribeFromPointsTo != null) {
        this.unsubscribeFromPointsTo();
    }

    this.pointsTo = observable;
    this.unsubscribeFromPointsTo = observable.rxForEach(value => {
        if (observable == this.pointsTo) {
            observableImpl.rxPush.call(this, value);
        }
    });
}

module.exports = {
    rxForEach: rxForEach,
    rxForEachOnce: rxForEachOnce,
    rxPointTo: rxPointTo,
    rxPush: rxPush,
    unsubscribe: unsubscribe
};
