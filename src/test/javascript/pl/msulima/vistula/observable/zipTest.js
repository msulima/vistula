"use strict";

const prodRequire = require("./prodRequire");

const vistula = prodRequire("pl/msulima/vistula/observable/observable");
const util = prodRequire("pl/msulima/vistula/observable/util");
const zip = prodRequire("pl/msulima/vistula/observable/zip");
const constantObservable = prodRequire("pl/msulima/vistula/observable/constantObservable");

const Probe = require("./probe").Probe;
const expect = require('chai').expect;

describe("zip", function () {

    it("zip empty list", function () {
        // given
        const Source = new vistula.ObservableImpl();
        const Initial = constantObservable.constantObservable(1);

        const Obs = zip.zip([]);
        const probe = new Probe(Obs);

        // when
        // then
        probe.expect([[]]);
    });

    it("allow to unsubscribe and subscribe again", function () {
        // given
        const observed = [];
        const First = new vistula.ObservableImpl();
        const Second = constantObservable.constantObservable(2);

        const Obs = zip.zip([First, Second]);
        Obs.rxForEachOnce(values => {
            observed.push(values);
        });

        // when
        First.rxPush(1);

        // then
        expect(observed).to.deep.equal([[1, 2]]);

        // given
        First.rxPush(3);
        Obs.rxForEachOnce(values => {
            observed.push(values);
        });

        // then
        expect(observed).to.deep.equal([[1, 2], [3, 2]]);
    });

    it("zipAndFlatten", function () {
        // given
        const Obs = zip.zipAndFlatten([
            constantObservable.constantObservable([1]),
            constantObservable.constantObservable([2, 3])
        ]);
        const probe = new Probe(Obs);

        // when & then
        probe.expect([[1, 2, 3]]);
    });
});
