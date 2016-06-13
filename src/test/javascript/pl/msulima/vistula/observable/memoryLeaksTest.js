"use strict";

const prodRequire = require("./prodRequire");

const vistula = prodRequire("pl/msulima/vistula/observable/observable");
const util = prodRequire("pl/msulima/vistula/observable/util");
const ifChangedArrays = prodRequire("pl/msulima/vistula/observable/ifChangedArrays");
const constantObservable = prodRequire("pl/msulima/vistula/observable/constantObservable");

const Probe = require("./probe").Probe;

const expect = require('chai').expect;

describe("memory leaks", function () {

    it("map should not unsubscribe from parent", function () {
        // given
        const Source = new vistula.ObservableImpl();
        const Obs = Source.rxMap(x => x * 10);

        const observed = [];

        // when
        const firstUnsubscribe = Obs.rxForEach(x => {
            observed.push(x);
        });
        Source.rxPush(1);
        firstUnsubscribe();

        // then
        expect(observed).to.deep.equal([10]);

        // when
        const secondUnsubscribe = Obs.rxForEach(x => {
            observed.push(x * 10);
        });
        Source.rxPush(2);
        secondUnsubscribe();

        // then
        expect(observed).to.deep.equal([10, 100, 200]);
        expect(Source.observers).to.have.length(0);
        expect(Obs.observers).to.be.undefined;
    });

    it("inside flatMap", function () {
        // given
        const Source = new vistula.ObservableImpl();
        const Other = constantObservable.constantObservable(10);
        const OtherCopy = Other.rxMap(x => x);
        const Obs = Source.rxFlatMap(x => OtherCopy.rxMap(y => x * y));
        const probe = new Probe(Obs);

        // when
        Source.rxPush(1);
        Source.rxPush(2);
        Source.rxPush(3);

        // then
        probe.expect([10, 20, 30]);
        expect(Source.observers).to.have.length(1);
        expect(Obs.observers).to.have.length(1);
        expect(Other.observers).to.have.length(1);
        expect(OtherCopy.observers).to.be.undefined;
    });

    it("outside flatMap", function () {
        // given
        const Source = new vistula.ObservableImpl();
        const FirstObs = Source.rxFlatMap(x => constantObservable.constantObservable(x * 10));
        const firstProbe = new Probe(FirstObs);
        const SecondObs = Source.rxFlatMap(x => constantObservable.constantObservable(x * 100));
        const secondProbe = new Probe(SecondObs);

        // when
        Source.rxPush(1);
        Source.rxPush(2);

        // then
        firstProbe.expect([10, 20]);
        secondProbe.expect([100, 200]);

        // when
        secondProbe.unsubscribe();
        Source.rxPush(3);

        // then
        firstProbe.expect([10, 20, 30]);
        secondProbe.expect([100, 200]);
        expect(Source.observers).to.have.length(1);
    });

    it("if", function () {
        // given
        const Left = [constantObservable.constantObservable(0), constantObservable.constantObservable(1)];
        const Right = [constantObservable.constantObservable(2)];
        const Condition = new vistula.ObservableImpl();

        const Obs = ifChangedArrays.ifChangedArrays(Condition, Left, Right);
        const probe = new Probe(Obs);

        // when
        Condition.rxPush(true);
        Condition.rxPush(false);
        Condition.rxPush(false);
        Condition.rxPush(true);
        Condition.rxPush(false);
        Condition.rxPush(true);

        // then
        expect(Obs.observers).to.have.length(1);
        expect(Obs.pointsTo.upstream.observers).to.have.length(1);
        expect(Left[0].observers).to.have.length(1);
        probe.expect([[0, 1], [2], [0, 1], [2], [0, 1]]);
    });
});
