"use strict";

const observableImpl = require("./observableImpl");
const mapObservable = require("./mapObservable");
const pointer = require("./pointer");

const ObservableImpl = function (upstreamUnsubscribe) {
    this.hasValue = false;
    this.lastValue = null;
    this.observers = [];
    this.upstreamUnsubscribe = upstreamUnsubscribe;
};

ObservableImpl.prototype.rxForEach = observableImpl.rxForEach;
ObservableImpl.prototype.rxForEachOnce = observableImpl.rxForEachOnce;
ObservableImpl.prototype.rxPush = observableImpl.rxPush;
ObservableImpl.prototype.unsubscribe = observableImpl.unsubscribe;

const MapObservable = function (upstream, transformation) {
    this.upstream = upstream;
    this.transformation = transformation;
};

MapObservable.prototype.rxForEach = mapObservable.rxForEach;
MapObservable.prototype.rxForEachOnce = mapObservable.rxForEachOnce;

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

PointerObservable.prototype.rxForEach = pointer.rxForEach;
PointerObservable.prototype.rxForEachOnce = pointer.rxForEachOnce;
PointerObservable.prototype.rxPointTo = pointer.rxPointTo;
PointerObservable.prototype.rxPush = pointer.rxPush;
PointerObservable.prototype.unsubscribe = pointer.unsubscribe;

ObservableImpl.prototype.rxMap = PointerObservable.prototype.rxMap = function (transformation) {
    return new MapObservable(this, transformation);
};
MapObservable.prototype.rxMap = mapObservable.rxMap;

ObservableImpl.prototype.rxFlatMap = PointerObservable.prototype.rxFlatMap = function (transformation) {
    return new PointerObservable(this, transformation);
};
MapObservable.prototype.rxFlatMap = mapObservable.rxFlatMap;


module.exports.PointerObservable = PointerObservable;
module.exports.ObservableImpl = ObservableImpl;
