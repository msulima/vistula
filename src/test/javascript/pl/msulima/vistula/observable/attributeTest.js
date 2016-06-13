"use strict";

const prodRequire = require("./prodRequire");

const ObservableImpl = prodRequire("pl/msulima/vistula/observable/observable").ObservableImpl;
const util = prodRequire("pl/msulima/vistula/observable/util");
const constantObservable = prodRequire("pl/msulima/vistula/observable/constantObservable");

const Probe = require("./probe").Probe;

describe("Attribute access", function () {

    it("access constant", function () {
        // given
        const Source = constantObservable.constantObservable({
            B: constantObservable.constantObservable(1)
        });
        const probe = new Probe(Source.rxFlatMap($arg => $arg.B));

        // when & then
        probe.expect([1]);
    });

    // FIXME Or is it?
    // it("modification of a view is allowed", function () {
    //     // given
    //     const Source = constantObservable.constantObservable(1).rxMap(x => x * 10);
    //     const Copy = Source.rxMap(x => x * 10);
    //     const probe = new Probe(Copy);
    //
    //     // when
    //     Source.rxPush(2);
    //
    //     // then
    //     probe.expect([100, 20]);
    // });

    it("copies of same field are entangled", function () {
        // given
        const Source = util.toObservable({
            A: 1
        });
        const Field = Source.rxFlatMap(x => x.A);
        const Copy = Source.rxFlatMap(x => x.A).rxMap(x => x * 10);
        const probe = new Probe(Copy);

        // when
        Field.rxPush(2);

        // then
        probe.expect([10, 20]);
    });

    it("modification of a flat map", function () {
        // given
        const Source = new ObservableImpl();
        const Mapped = Source.rxFlatMap(i => util.toObservable({
            A: i * 10
        }));

        const Field = Mapped.rxFlatMap(x => x.A);
        const Copy = Mapped.rxFlatMap(x => x.A);

        const probe = new Probe(Copy);

        // when
        Source.rxPush(1);
        Field.rxPush(20);
        Source.rxPush(3);

        // then
        probe.expect([10, 20, 30]);
    });

    it("multiple modifications of a flat map", function () {
        // given
        const A = new ObservableImpl();
        const B = constantObservable.constantObservable({
            C: A
        });
        const Source = constantObservable.constantObservable({
            D: B.rxFlatMap($arg => $arg.C)
        });

        const Field = Source.rxFlatMap(x => x.D);
        const Copy = Source.rxFlatMap(x => x.D);

        const probe = new Probe(Copy);

        // when
        Field.rxPush(1);
        A.rxPush(2);
        Field.rxPush(3);

        // then
        probe.expect([1, 2, 3]);
    });
});
