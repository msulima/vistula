'use strict';

let vistula = require('../../main/javascript/observable');
let vistulaUtil = require('../../main/javascript/util');

let Probe = require('./probe').Probe;


describe("util.ifStatement", function () {

    it("if", function () {
        // given
        let Left = vistulaUtil.constantObservable(0);
        let Right = vistulaUtil.constantObservable(1);
        const Condition = new vistula.ObservableImpl();

        const Obs = vistulaUtil.ifStatement(Condition, Left, Right);
        const probe = new Probe(Obs);

        // when
        Condition.rxPush(true);
        Condition.rxPush(true);
        Condition.rxPush(false);
        Condition.rxPush(true);

        // then
        probe.expect([0, 1, 0]);
    });

    it("complex, nested if", function () {
        // given
        const Condition = new vistula.ObservableImpl();
        const ConditionCopy = Condition.rxMap($arg => $arg);

        Condition.marker = "Condition";
        ConditionCopy.marker = "ConditionCopy";

        const probe = new Probe(vistulaUtil.ifStatement(
            ConditionCopy,
            vistulaUtil.constantObservable(10),
            vistulaUtil.ifStatement(
                Condition,
                vistulaUtil.constantObservable(11),
                vistulaUtil.constantObservable(20)
            )
        ));

        // when
        Condition.rxPush(true);
        Condition.rxPush(false);
        Condition.rxPush(true);

        // then
        probe.expect([10, 11, 20, 10]);
    });
});
