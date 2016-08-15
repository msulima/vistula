"use strict";

const prodRequire = require("./prodRequire");

const vistula = prodRequire("pl/msulima/vistula/observable/observable");
const util = prodRequire("pl/msulima/vistula/observable/util");
const zip = prodRequire("pl/msulima/vistula/observable/zip");
const constantObservable = prodRequire("pl/msulima/vistula/observable/constantObservable");
const expect = require("chai").expect;

const Probe = require("./probe").Probe;


describe("aggregates", function () {

    it("aggregate", function () {
        // given
        const Source = new vistula.ObservableImpl();

        const Obs = util.aggregate(Source, 100, (acc, source) => {
            return constantObservable.constantObservable(acc - source);
        });
        const probe = new Probe(Obs);

        // when
        Source.rxPush(10);
        Source.rxPush(1);

        // then
        probe.expect([100, 90, 89]);
    });

    it("withDefault", function () {
        // given
        const Source = new vistula.ObservableImpl();

        const Obs = util.withDefault(Source, 1);
        const probe = new Probe(Obs);

        // when
        Source.rxPush(10);
        Source.rxPush(100);

        // then
        probe.expect([1, 10, 100]);
        probe.unsubscribe();
        expect(Source.observers).to.have.length(0);

        // when
        const probe2 = new Probe(Obs);
        Source.rxPush(200);
        Source.rxPush(300);

        // then
        probe2.expect([100, 200, 300]);
    });
});
