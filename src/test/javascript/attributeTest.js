'use strict';

let ObservableImpl = require('../../main/javascript/observable').ObservableImpl;
let vistulaUtil = require('../../main/javascript/util');

let Probe = require('./probe').Probe;
var expect = require('chai').expect;


describe("Attribute access", function () {

    it("modification of a view is allowed", function () {
        // given
        let Source = vistulaUtil.constantObservable(1).rxMap(x => x * 10);
        let Copy = Source.rxMap(x => x * 10);
        let probe = new Probe(Copy);

        // when
        Source.rxPush(2);

        // then
        probe.expect([100, 20]);
    });

    it("copies of same field are entangled", function () {
        // given
        let Source = vistulaUtil.toObservable({
            A: 1
        });
        let Field = Source.rxFlatMap(x => x.A);
        let Copy = Source.rxFlatMap(x => x.A).rxMap(x => x * 10);
        let probe = new Probe(Copy);

        // when
        Field.rxPush(2);

        // then
        probe.expect([10, 20]);
    });

    it("modification of a flat map", function () {
        // given
        let Source = new ObservableImpl();
        let Mapped = Source.rxFlatMap(i => vistulaUtil.toObservable({
            A: i * 10
        }));

        let Field = Mapped.rxFlatMap(x => x.A);
        let Copy = Mapped.rxFlatMap(x => x.A);

        let probe = new Probe(Copy);

        // when
        Source.rxPush(1);
        Field.rxPush(20);
        Source.rxPush(3);

        // then
        probe.expect([10, 20, 30]);
    });
});
