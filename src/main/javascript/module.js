'use strict';

let observable = require('./observable');
let util = require('./util');
let dom = require('./dom');

let combinedExports = {};
Object.assign(combinedExports, observable, util, {
    dom: dom
});

module.exports = combinedExports;
