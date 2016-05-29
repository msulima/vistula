'use strict';

const vistula = require("../../main/javascript/observable");
const util = require("../../main/javascript/util");
const ifChangedArrays = require("../../main/javascript/ifChangedArrays");

const Probe = require("./probe").Probe;

const chai = require("chai");
chai.config.truncateThreshold = 0;

describe("util.ifChangedArrays", function () {

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
        Condition.rxPush(true);

        // then
        probe.expect([[0, 1], [2], [0, 1], [2], [0, 1]]);
    });
    //
    // it("complex, nested if", function () {
    //     // given
    //     const Condition = new vistula.ObservableImpl();
    //     const ConditionCopy = Condition.rxMap($arg => $arg);
    //
    //     Condition.marker = "Condition";
    //     ConditionCopy.marker = "ConditionCopy";
    //
    //     const probe = new Probe(util.ifChangedArrays(
    //         ConditionCopy,
    //         util.constantObservable(10),
    //         util.ifChangedArrays(
    //             Condition,
    //             util.constantObservable(11),
    //             util.constantObservable(20)
    //         )
    //     ));
    //
    //     // when
    //     Condition.rxPush(true);
    //     Condition.rxPush(false);
    //     Condition.rxPush(true);
    //
    //     // then
    //     probe.expect([10, 11, 20, 10]);
    // });
});
