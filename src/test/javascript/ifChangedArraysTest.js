"use strict";

const vistula = require("../../main/javascript/observable");
const util = require("../../main/javascript/util");
const ifChangedArrays = require("../../main/javascript/ifChangedArrays");

const Probe = require("./probe").Probe;

const chai = require("chai");
chai.config.truncateThreshold = 0;

describe("util.ifChangedArrays", function () {

    it("if", function () {
        // given
        const Source = new vistula.ObservableImpl();
        Source.rxPush(1);
        const Left = [util.constantObservable(0), Source];
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
        Source.rxPush(3);
        Condition.rxPush(true);

        // then
        probe.expect([[0, 1], [2], [0, 1], [2], [0, 1], [0, 3]]);
    });
});
