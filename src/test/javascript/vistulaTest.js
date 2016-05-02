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
        Obs.onNext(1);

        // then
        probe.expect([1]);
    });

    it("map", function () {
        // given
        let Obs = new vistula.ObservableImpl();

        let Mapped = Obs.map((value) => {
            return value * 10;
        });
        let probe = new Probe(Mapped);

        // when
        Obs.onNext(1);

        probe.expect([10]);
    });

    it("flatMap", function () {
        // given
        let Obs = new vistula.ObservableImpl();
        let nestedObservable = new vistula.ObservableImpl();

        let Mapped = Obs.flatMap((value) => {
            return nestedObservable.map((nested) => {
                return value + nested;
            });
        });
        let probe = new Probe(Mapped);

        // when
        Obs.onNext(1);
        nestedObservable.onNext(2);

        // then
        probe.expect([3]);
    });

    it("aggregate", function () {
        // given
        let Source = new vistula.ObservableImpl();
        var Initial = vistulaUtil.constantObservable(1);

        let Obs = vistulaUtil.aggregate(Initial, Source, ($acc, $source) => {
            //noinspection UnnecessaryLocalVariableJS
            let Obs = vistulaUtil.constantObservable($acc);
            let Source = vistulaUtil.constantObservable($source);
            return vistulaUtil.zip([Obs, Source]).map((value) => {
                return value[0] + value[1];
            });
        });
        let probe = new Probe(Obs);

        // when
        Source.onNext(10);
        Source.onNext(100);

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
        Condition.onNext(true);
        Condition.onNext(true);
        Condition.onNext(false);
        Condition.onNext(true);

        // then
        probe.expect([0, 0, 1, 0]);
    });
});
