'use strict';

const vistula = require("../../main/javascript/observable");
const util = require("../../main/javascript/util");

const Probe = require("./probe").Probe;


describe("util.ifStatement", function () {

    it("if", function () {
        // given
        const Left = new vistula.ObservableImpl();
        Left.rxPush(1);
        const Right = util.constantObservable(2);
        const Condition = new vistula.ObservableImpl();

        const Obs = util.ifStatement(Condition, Left.rxMap(x => x * 10), Right);
        const probe = new Probe(Obs);

        // when
        Condition.rxPush(true);
        Condition.rxPush(true);
        Condition.rxPush(false);
        Condition.rxPush(true);

        Left.rxPush(3);

        // then
        probe.expect([10, 2, 10, 30]);
    });

    it("complex, nested if", function () {
        // given
        const Condition = new vistula.ObservableImpl();
        const ConditionCopy = Condition.rxMap($arg => $arg);

        const probe = new Probe(util.ifStatement(
            ConditionCopy,
            util.constantObservable(10),
            util.ifStatement(
                Condition,
                util.constantObservable(11),
                util.constantObservable(20)
            )
        ));

        // when
        Condition.rxPush(true);
        Condition.rxPush(false);

        // then
        probe.expect([10, 20]);

        // when
        Condition.rxPush(true);

        // then
        probe.expect([10, 20, 10]);
    });
});
