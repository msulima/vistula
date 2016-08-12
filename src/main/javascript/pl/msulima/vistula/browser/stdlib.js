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

function ajaxGet(Url) {
    const obs = new vistula.ObservableImpl();

    return Url.rxFlatMap(function (url) {
        var request = new XMLHttpRequest();
        request.onreadystatechange = function () {
            var DONE = this.DONE || 4;
            if (this.readyState === DONE) {
                var value = vistula.toObservable(JSON.parse(this.responseText));
                value.rxForEach(obs.rxPush.bind(obs));
            }
        };
        request.open("GET", url, true);
        request.send(null);

        return obs;
    });
}

function formValueFromEvent(_ev) {
    let ev = _ev.rxLastValue();
    let elements = ev.srcElement.elements;
    let formValue = {};
    Object.keys(elements).forEach(function (key) {
        formValue[elements[key].name] = vistula.toObservable(elements[key].value);
    });
    Object.keys(elements).forEach(function (key) {
        elements[key].value = "";
    });
    return vistula.constantObservable(formValue);
}

const stdlib = vistula.toObservable({
    dom: {
        appendChild: appendChild
    },
    net: {
        ajaxGet: ajaxGet
    },
    storage: require("./storage"),
    ajaxGet: ajaxGet,
    appendChild: appendChild,
    formValueFromEvent: formValueFromEvent,
});

// FIXME
stdlib.lastValue.location = vistula.constantObservable(require("./location"));

module.exports = stdlib;
