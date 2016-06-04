'use strict';

const vistula = require("../../main/javascript/observable");
const util = require("../../main/javascript/util");
const zip = require("../../main/javascript/zip");

const Probe = require("./probe").Probe;
const expect = require('chai').expect;

describe("zip", function () {

    it("zip empty list", function () {
        // given
        const Source = new vistula.ObservableImpl();
        const Initial = util.constantObservable(1);

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
        const Second = util.constantObservable(2);

        const Obs = zip.zip([First, Second]);
        Obs.rxForEachOnce(values => {
            observed.push(values);
        });

        // when
        First.rxPush(1);

        // then
        expect(observed).to.deep.equal([[1, 2]]);

        // given
        Obs.rxForEachOnce(values => {
            observed.push(values);
        });

        // when
        First.rxPush(3);

        // then
        expect(observed).to.deep.equal([[1, 2], [3, 2]]);
    });

    it("zipAndFlatten", function () {
        // given
        const Obs = zip.zipAndFlatten([
            util.constantObservable([1]),
            util.constantObservable([2, 3])
        ]);
        const probe = new Probe(Obs);

        // when & then
        probe.expect([[1, 2, 3]]);
    });
});
