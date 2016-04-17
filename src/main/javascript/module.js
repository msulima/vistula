'use strict';

let observable = require('./observable');
let util = require('./util');

let combinedExports = {};
Object.assign(combinedExports, observable, util);

module.exports = combinedExports;
