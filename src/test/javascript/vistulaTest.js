'use strict';

let vistula = require('../../main/javascript/observable');
let vistulaUtil = require('../../main/javascript/util');

let Probe = require('./probe').Probe;


describe("Observable", function () {

    it("return last value", function () {
        // given
        let Obs = new vistula.ObservableImpl();
        let probe = new Probe(Obs);

        // when
        Obs.rxPush(1);

        // then
        probe.expect([1]);
    });

    it("map", function () {
        // given
        let Obs = new vistula.ObservableImpl();

        let Mapped = Obs.rxMap((value) => {
            return value * 10;
        });
        let probe = new Probe(Mapped);

        // when
        Obs.rxPush(1);

        probe.expect([10]);
    });

    it("flatMap", function () {
        // given
        let Obs = new vistula.ObservableImpl();
        let nestedObservable = new vistula.ObservableImpl();

        let Mapped = Obs.rxFlatMap((value) => {
            return nestedObservable.rxMap((nested) => {
                return value + nested;
            });
        });
        let probe = new Probe(Mapped);

        // when
        Obs.rxPush(1);
        nestedObservable.rxPush(2);

        // then
        probe.expect([3]);
    });

    it("zip empty list", function () {
        // given
        let Source = new vistula.ObservableImpl();
        var Initial = vistulaUtil.constantObservable(1);

        let Obs = vistulaUtil.zip([]);
        let probe = new Probe(Obs);

        // when
        // then
        probe.expect([[]]);
    });

    it("aggregate", function () {
        // given
        let Source = new vistula.ObservableImpl();
        var Initial = vistulaUtil.constantObservable(1);

        let Obs = vistulaUtil.aggregate(Initial, Source, ($acc, $source) => {
            //noinspection UnnecessaryLocalVariableJS
            let Obs = vistulaUtil.constantObservable($acc);
            let Source = vistulaUtil.constantObservable($source);
            return vistulaUtil.zip([Obs, Source]).rxMap((value) => {
                return value[0] + value[1];
            });
        });
        let probe = new Probe(Obs);

        // when
        Source.rxPush(10);
        Source.rxPush(100);

        // then
        probe.expect([1, 11, 111]);
    });

    it("if", function () {
        // given
        let Left = vistulaUtil.constantObservable(0);
        let Right = vistulaUtil.constantObservable(1);
        let Condition = new vistula.ObservableImpl();

        let Obs = vistulaUtil.ifStatement(Condition, Left, Right);
        let probe = new Probe(Obs);

        // when
        Condition.rxPush(true);
        Condition.rxPush(true);
        Condition.rxPush(false);
        Condition.rxPush(true);

        // then
        probe.expect([0, 0, 1, 0]);
    });

    it("toObservable", function () {
        // given
        let obj = {
            "A": {
                "B": 1
            },
            "C": 2,
            "D": [
                3
            ]
        };

        let Obs = vistulaUtil.toObservable(obj);

        // when
        let Flat = Obs.rxFlatMap((obj) => {
            return obj.C;
        });
        let Nested = Obs.rxFlatMap((obj) => {
            return obj.A.rxFlatMap((a) => {
                return a.B;
            })
        });
        let IsList = Obs.rxFlatMap((obj) => {
            return obj.D.rxMap((list) => {
                return Array.isArray(list);
            });
        });
        let List = Obs.rxFlatMap((obj) => {
            return obj.D.rxFlatMap((list) => {
                return list[0];
            });
        });
        let flatProbe = new Probe(Flat);
        let nestedProbe = new Probe(Nested);
        let isListProbe = new Probe(IsList);
        let listProbe = new Probe(List);

        // then
        flatProbe.expect([2]);
        nestedProbe.expect([1]);
        isListProbe.expect([true]);
        listProbe.expect([3]);
    });
});
