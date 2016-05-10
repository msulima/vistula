'use strict';

let vistulaUtil = require('../../main/javascript/util');

let Probe = require('./probe').Probe;
var expect = require('chai').expect;


describe("Attribute access", function () {

    it("copies of variable are entangled", function () {
        // given
        let Source = vistulaUtil.toObservable({
            A: 1
        });
        let Field = Source.rxFlatMap(x => x.A);
        let Copy = Source.rxFlatMap(x => x.A).rxMap(x => x * 10);
        let probe = new Probe(Copy);

        // when
        Field.rxSet(2);

        // then
        probe.expect([10, 20]);
    });

    it("modification of a view is not allowed", function () {
        // given
        let Source = vistulaUtil.toObservable({
            A: 1
        });
        let Field = Source.rxFlatMap(x => x.A).rxMap(x => x * 10);
        let Copy = Source.rxFlatMap(x => x.A);
        let probe = new Probe(Copy);

        // when & then
        expect(() => Field.rxSet(2)).to.throw("Cannot set value");
        probe.expect([1]);
    });
});
