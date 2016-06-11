"use strict";

function rxForEach(callback) {
    return this.upstream.rxForEach(value => {
        callback(this.transformation(value));
    });
}

function rxForEachOnce(callback) {
    return this.upstream.rxForEachOnce(value => {
        callback(this.transformation(value));
    });
}

function rxMap(transformation) {
    return this.upstream.rxMap(value => {
        return transformation(this.transformation(value));
    });
}

function rxFlatMap(transformation) {
    return this.upstream.rxFlatMap(value => {
        return transformation(this.transformation(value));
    });
}

module.exports = {
    rxFlatMap: rxFlatMap,
    rxForEach: rxForEach,
    rxForEachOnce: rxForEachOnce,
    rxMap: rxMap
};
