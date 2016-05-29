'use strict';

const vistula = require("../../main/javascript/observable");
const util = require("../../main/javascript/util");
const ifChangedArrays = require("../../main/javascript/ifChangedArrays");

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

    it("flatMap", function () {
        // given
        const Source = new vistula.ObservableImpl();
        const Other = util.constantObservable(10);
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

    it("if", function () {
        // given
        const Left = [util.constantObservable(0), util.constantObservable(1)];
        const Right = [util.constantObservable(2)];
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
        expect(Obs.observers).to.be.undefined;
        expect(Obs.upstream.observers).to.have.length(1);
        expect(Obs.upstream.pointsTo.observers).to.have.length(1);
        expect(Left[0].observers).to.have.length(1);
        probe.expect([[0, 1], [2], [0, 1], [2], [0, 1]]);
    });
});
