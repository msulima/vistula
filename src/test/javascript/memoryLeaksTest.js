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
        const probe = new Probe(Obs);

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
    });
});
