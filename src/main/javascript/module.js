"use strict";

const observable = require("./observable");
const util = require("./util");
const ifChangedArrays = require("./ifChangedArrays");
const dom = require("./dom");
const zip = require("./zip");
const staticObservable = require("./static");

const combinedExports = {};
Object.assign(combinedExports, observable, util, ifChangedArrays, zip, staticObservable, {
    dom: dom
});

module.exports = combinedExports;
