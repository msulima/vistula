'use strict';

var ConstantObservable = function (value) {
    this.value = value;
};

ConstantObservable.prototype.rxForEach = function (callback) {
    callback(this.value, this.unsubscribe.bind(this, callback));
};

ConstantObservable.prototype.rxPush = function (_) {
    throw "Cannot push to constant";
};

ConstantObservable.prototype.unsubscribe = function (_) {
};

ConstantObservable.prototype.rxMap = function (callback) {
    return new ConstantObservable(callback(this.value));
};

ConstantObservable.prototype.rxFlatMap = function (callback) {
    return callback(this.value);
};

module.exports.ConstantObservable = ConstantObservable;
