"use strict";

const common = require("./common");

const PointerObservable = function (upstream, transformation) {
    this.hasValue = false;
    this.lastValue = null;
    this.observers = [];

    this.upstream = upstream;
    this.transformation = transformation;

    this.isSubscribed = false;
    this.pointsTo = null;
    this.unsubscribeFromPointsTo = null;
    this.unsubscribeFromUpstream = null;
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

ObservableImpl.prototype.rxForEach = common.rxForEach;

ObservableImpl.prototype.rxForEachOnce = common.rxForEachOnce;

PointerObservable.prototype.rxForEach = function (callback) {
    if (!this.isSubscribed) {
        this.isSubscribed = true;
        this.unsubscribeFromUpstream = this.upstream.rxForEach(next => {
            this.rxPointTo(this.transformation(next));
        });
    }

    return common.rxForEach.call(this, callback);
};

PointerObservable.prototype.rxForEachOnce = function (callback) {
    if (!this.isSubscribed) {
        this.isSubscribed = true;
        this.unsubscribeFromUpstream = this.upstream.rxForEach(next => {
            this.rxPointTo(this.transformation(next));
        });
    }

    return common.rxForEachOnce.call(this, callback);
};

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

    this.observers.forEach(obs => obs(value));
};

PointerObservable.prototype.rxPush = function (value) {
    if (this.pointsTo == null) {
        this.rxForEachOnce(() => {
        });

        if (this.pointsTo == null) {
            throw "Cannot push to unset pointer";
        }
    }

    this.pointsTo.rxPush(value);
};

ObservableImpl.prototype.unsubscribe = common.unsubscribe;

PointerObservable.prototype.unsubscribe = function (callback) {
    common.unsubscribe.call(this, callback);

    if (this.observers.length == 0) {
        this.isSubscribed = false;
        this.unsubscribeFromUpstream();
        this.unsubscribeFromUpstream = null;

        if (this.unsubscribeFromPointsTo != null) {
            this.unsubscribeFromPointsTo();
            this.unsubscribeFromPointsTo = null;
        }
    }
};

ObservableImpl.prototype.rxMap = PointerObservable.prototype.rxMap = function (transformation) {
    return new MapObservable(this, transformation);
};

ObservableImpl.prototype.rxFlatMap = PointerObservable.prototype.rxFlatMap = function (transformation) {
    return new PointerObservable(this, transformation);
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
