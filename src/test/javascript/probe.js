'use strict';

const expect = require('chai').expect;

function Probe(Observable) {
    const observed = [];
    this.observed = observed;

    Observable.rxForEach(value => {
        observed.push(value);
    });
}

Probe.prototype.expect = function (values) {
    expect(this.observed).to.deep.equal(values);
};

module.exports = {
    Probe: Probe
};
