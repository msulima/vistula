'use strict';

var expect = require('chai').expect;

function Probe(observable) {
    let observed = [];
    this.observed = observed;

    observable.forEach((value) => {
        observed.push(value);
    });
}

Probe.prototype.expect = function (values) {
    expect(values).to.deep.equal(this.observed);
};

module.exports = {
    Probe: Probe
};
