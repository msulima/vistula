'use strict';

function textObservable(Obs) {
    let span = document.createElement("span");
    Obs.forEach(function ($arg) {
        span.textContent = $arg;
    });
    return span;
}

function createElement(parent, childNodes) {
    childNodes.forEach(parent.appendChild.bind(parent));
    return parent;
}

module.exports = {
    createElement: createElement,
    textObservable: textObservable
};
