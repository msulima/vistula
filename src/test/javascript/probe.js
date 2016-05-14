'use strict';

var expect = require('chai').expect;

function Probe(observable) {
    let observed = [];
    this.observed = observed;

    observable.rxForEach((value) => {
        observed.push(value);
    });
}

Probe.prototype.expect = function (values) {
    expect(this.observed).to.deep.equal(values);
};

module.exports = {
    Probe: Probe
};
