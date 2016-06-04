'use strict';

const expect = require('chai').expect;

function Probe(Observable) {
    const observed = [];
    this.upstream = Observable;
    this.observed = observed;

    this.subscribe();
}

Probe.prototype.expect = function (values) {
    expect(this.observed).to.deep.equal(values);
};

Probe.prototype.subscribe = function () {
    this.unsubscribe = this.upstream.rxForEach(value => {
        this.observed.push(value);
    });
};

module.exports = {
    Probe: Probe
};
