"use strict";

const prodRequire = require("./prodRequire");

const vistula = prodRequire("pl/msulima/vistula/observable/observable");
const staticObservable = prodRequire("pl/msulima/vistula/observable/static");

const Probe = require("./probe").Probe;
const expect = require('chai').expect;

describe("staticObservable", function () {

    it("ignores updates, unsubscribes from parent", function () {
        // given
        const Source = new vistula.ObservableImpl();

        const Obs = staticObservable.staticValue(Source, "value");
        const probe = new Probe(Obs);

        // when
        Source.rxPush(1);
        Source.rxPush(2);
        probe.unsubscribe();

        // then
        probe.expect(["value"]);
        expect(Source.observers).to.be.empty;

        // when
        probe.subscribe();

        // then
        expect(Source.observers).to.have.length(1);
    });

    it("transforms in place", function () {
        // given
        const Source = new vistula.ObservableImpl();

        const observed = [];
        const Obs = staticObservable.staticTransform(Source, value => {
            observed.push(value * 10)
        });
        const probe = new Probe(Obs);

        // when
        Source.rxPush(1);
        Source.rxPush(2);
        probe.unsubscribe();

        // then
        probe.expect([null]);
        expect(observed).to.deep.equal([10, 20]);
    });
});
