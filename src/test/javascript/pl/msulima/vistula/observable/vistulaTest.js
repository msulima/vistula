"use strict";

const prodRequire = require("./prodRequire");

const vistula = prodRequire("pl/msulima/vistula/observable/observable");
const util = prodRequire("pl/msulima/vistula/observable/util");
const zip = prodRequire("pl/msulima/vistula/observable/zip");
const constantObservable = prodRequire("pl/msulima/vistula/observable/constantObservable");
const expect = require("chai").expect;

const Probe = require("./probe").Probe;


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

    it("for each once is lazy", function () {
        // given
        const Obs = new vistula.ObservableImpl();
        const observed = [];

        Obs.rxForEachOnce(value => {
            observed.push(value);
        });

        // when
        expect(observed).to.be.empty;
        Obs.rxPush(1);
        Obs.rxPush(2);

        // then
        expect(observed).to.deep.equal([1]);
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

    it("aggregate", function () {
        // given
        const Source = new vistula.ObservableImpl();
        const Initial = constantObservable.constantObservable(1);

        const Obs = util.aggregate(Initial, Source, ($acc, $source) => {
            //noinspection UnnecessaryLocalVariableJS
            const Obs = constantObservable.constantObservable($acc);
            const Source = constantObservable.constantObservable($source);
            return zip.zip([Obs, Source]).rxMap((value) => {
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

    it("flatMap then map", function () {
        // given
        const Source = new vistula.ObservableImpl();
        const Source2 = Source.rxFlatMap(x => constantObservable.constantObservable(x * 10)).rxMap(x => x * 5);
        const Obs = Source.rxFlatMap(x => Source2);
        const probe = new Probe(Obs);

        // when
        Source.rxPush(1);
        Source.rxPush(2);

        // then
        probe.expect([50, 100]);

        // when
        probe.unsubscribe();
        Source.rxPush(3);
        probe.subscribe();

        // then
        probe.expect([50, 100, 150]);
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

        const Obs = util.toObservable(obj);

        // when
        const Flat = Obs.rxFlatMap((obj) => {
            return obj.C;
        });
        const Nested = Obs.rxFlatMap((obj) => {
            return obj.A.rxFlatMap((a) => {
                return a.B;
            })
        });
        const List = Obs.rxFlatMap((obj) => {
            return obj.D.rxFlatMap((list) => {
                return list.elements[0];
            });
        });
        const flatProbe = new Probe(Flat);
        const nestedProbe = new Probe(Nested);
        const listProbe = new Probe(List);

        // then
        flatProbe.expect([2]);
        nestedProbe.expect([1]);
        listProbe.expect([3]);
    });

    it("fromObservable", function () {
        // given
        const obj = [{
            "A": 1
        }];

        const Source = util.toObservable(obj);

        // when
        const Obs = util.fromObservable(Source);
        const probe = new Probe(Obs);

        // then
        probe.expect([obj]);
    });
});
