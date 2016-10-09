"use strict";

function appendChild(Target, Observables) {
    let currentChildren = [];

    return vistula.zip([Target, Observables]).rxMap(function ($args) {
        const target = document.getElementById($args[0]);
        const nextChildren = $args[1];
        vistula.dom.updateChildren(target, 0, currentChildren, nextChildren);
        currentChildren = nextChildren;
    });
}

const stdlib = vistula.toObservable({
    dom: {
        appendChild: appendChild
    },
    storage: require("./storage"),
    appendChild: appendChild,
});

// FIXME
stdlib.lastValue.location = vistula.constantObservable(require("./location"));
stdlib.lastValue.time = require("./time");

module.exports = stdlib;
