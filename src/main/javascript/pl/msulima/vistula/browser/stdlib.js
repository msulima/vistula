"use strict";

const stdlib = vistula.toObservable({
});

// FIXME
stdlib.lastValue.location = vistula.constantObservable(require("./location"));
stdlib.lastValue.time = require("./time");

module.exports = stdlib;
