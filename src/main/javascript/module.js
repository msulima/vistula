'use strict';

let observable = require("./observable");
let util = require("./util");
let ifChangedArrays = require("./ifChangedArrays");
let dom = require("./dom");

let combinedExports = {};
Object.assign(combinedExports, observable, util, ifChangedArrays, {
    dom: dom
});

module.exports = combinedExports;
