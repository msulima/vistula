"use strict";

const observable = require("./observable");
const util = require("./util");
const ifChangedArrays = require("./ifChangedArrays");
const dom = require("./dom");
const zip = require("./zip");
const staticObservable = require("./static");
const constantObservable = require("./constantObservable");

const combinedExports = {};
Object.assign(combinedExports, observable, util, ifChangedArrays, zip, staticObservable, constantObservable, {
    dom: dom
});

module.exports = combinedExports;
