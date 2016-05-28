'use strict';

const vistula = require('../../main/javascript/observable');
const vistulaUtil = require('../../main/javascript/util');
const expect = require('chai').expect;

const Probe = require('./probe').Probe;


describe("Observable", function () {

    it("return last value", function () {
        // given
        const Obs = new vistula.ObservableImpl();
        const probe = new Probe(Obs);

        // when
        Obs.rxPush(1);

        // then
        probe.expect([1]);
    });

    it("unsubscribe", function () {
        // given
        const Obs = new vistula.ObservableImpl();
        const observed = [];

        const unsubscribe = Obs.rxForEach((value) => {
            observed.push(value);
        });

        // when
        Obs.rxPush(1);
        Obs.rxPush(2);
        unsubscribe();
        Obs.rxPush(3);
        Obs.rxPush(4);

        // then
        expect(observed).to.deep.equal([1, 2]);
    });

    it("map", function () {
        // given
        const Obs = new vistula.ObservableImpl();

        const Mapped = Obs.rxMap((value) => {
            return value * 10;
        });
        const probe = new Probe(Mapped);

        // when
        Obs.rxPush(1);

        probe.expect([10]);
    });

    it("flatMap", function () {
        // given
        const Obs = new vistula.ObservableImpl();
        const nestedObservable = new vistula.ObservableImpl();

        const Mapped = Obs.rxFlatMap((value) => {
            return nestedObservable.rxMap((nested) => {
                return value + nested;
            });
        });
        const probe = new Probe(Mapped);

        // when
        Obs.rxPush(1);
        nestedObservable.rxPush(2);

        // then
        probe.expect([3]);
    });

    it("zip empty list", function () {
        // given
        const Source = new vistula.ObservableImpl();
        const Initial = vistulaUtil.constantObservable(1);

        const Obs = vistulaUtil.zip([]);
        const probe = new Probe(Obs);

        // when
        // then
        probe.expect([[]]);
    });

    it("zipAndFlatten", function () {
        // given
        const Obs = vistulaUtil.zipAndFlatten([
            vistulaUtil.constantObservable([1]),
            vistulaUtil.constantObservable([2, 3])
        ]);
        const probe = new Probe(Obs);

        // when & then
        probe.expect([[1, 2, 3]]);
    });

    it("aggregate", function () {
        // given
        const Source = new vistula.ObservableImpl();
        const Initial = vistulaUtil.constantObservable(1);

        const Obs = vistulaUtil.aggregate(Initial, Source, ($acc, $source) => {
            //noinspection UnnecessaryLocalVariableJS
            const Obs = vistulaUtil.constantObservable($acc);
            const Source = vistulaUtil.constantObservable($source);
            return vistulaUtil.zip([Obs, Source]).rxMap((value) => {
                return value[0] + value[1];
            });
        });
        const probe = new Probe(Obs);

        // when
        Source.rxPush(10);
        Source.rxPush(100);

        // then
        probe.expect([1, 11, 111]);
    });

    it("toObservable", function () {
        // given
        const obj = {
            "A": {
                "B": 1
            },
            "C": 2,
            "D": [
                3
            ]
        };

        const Obs = vistulaUtil.toObservable(obj);

        // when
        const Flat = Obs.rxFlatMap((obj) => {
            return obj.C;
        });
        const Nested = Obs.rxFlatMap((obj) => {
            return obj.A.rxFlatMap((a) => {
                return a.B;
            })
        });
        const IsList = Obs.rxFlatMap((obj) => {
            return obj.D.rxMap((list) => {
                return Array.isArray(list);
            });
        });
        const List = Obs.rxFlatMap((obj) => {
            return obj.D.rxFlatMap((list) => {
                return list[0];
            });
        });
        const flatProbe = new Probe(Flat);
        const nestedProbe = new Probe(Nested);
        const isListProbe = new Probe(IsList);
        const listProbe = new Probe(List);

        // then
        flatProbe.expect([2]);
        nestedProbe.expect([1]);
        isListProbe.expect([true]);
        listProbe.expect([3]);
    });
});
