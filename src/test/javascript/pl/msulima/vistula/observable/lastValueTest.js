"use strict";

const prodRequire = require("./prodRequire");

const vistula = prodRequire("pl/msulima/vistula/observable/observable");
const util = prodRequire("pl/msulima/vistula/observable/util");
const constantObservable = prodRequire("pl/msulima/vistula/observable/constantObservable");
const zip = prodRequire("pl/msulima/vistula/observable/zip");
const expect = require('chai').expect;

describe("*.rxLastValue()", function () {

    it("returns last value, also indirect", function () {
        // given
        const Pure = constantObservable.constantObservable(1);
        const Map = Pure.rxMap(x => x * 10);
        const Pointer = Pure.rxFlatMap(x => {
            return constantObservable.constantObservable(x * 100);
        });
        const Zip = zip.zip([Map, Pointer]);

        // when & then
        expect(Pure.rxLastValue()).to.equal(1);
        expect(Map.rxLastValue()).to.equal(10);
        expect(Pointer.rxLastValue()).to.equal(100);
        expect(Zip.rxLastValue()).to.deep.equal([10, 100]);
    });
});
