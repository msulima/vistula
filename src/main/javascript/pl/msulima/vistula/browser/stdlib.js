"use strict";

const stdlib = vistula.toObservable({
    storage: require("./storage"),
});

// FIXME
stdlib.lastValue.location = vistula.constantObservable(require("./location"));
stdlib.lastValue.time = require("./time");

module.exports = stdlib;
