'use strict';

var ObservableImpl = require('./observable').ObservableImpl;
var util = require("./util");

function ifStatement(Condition, FragTrue, FragFalse) {
    let proxy = new ObservableImpl();
    let lastValue = null;

    Condition.forEach(function ($condition) {
        if (lastValue != $condition) {
            lastValue = $condition;
            proxy.onNext($condition ? FragTrue : FragFalse);
        }
    });

    return proxy.flatMap(function ($fragments) {
        return util.zip($fragments).map(function ($arrays) {
            return [].concat.apply([], $arrays);
        });
    });
}

function textNode(text) {
    return util.constantObservable([document.createTextNode(text)]);
}

function textObservable(Obs) {
    let span = document.createElement("span");
    Obs.forEach(function ($arg) {
        span.textContent = $arg;
    });
    return util.constantObservable([span]);
}

function updateChildren(parent, currentChildren, nextChildren) {
    var currentLength = currentChildren.length;
    var nextLength = nextChildren.length;

    for (var i = 0; i < Math.min(currentLength, nextLength); i++) {
        parent.replaceChild(nextChildren[i], currentChildren[i]);
    }

    if (currentLength < nextLength) {
        for (i = currentLength; i < nextLength; i++) {
            parent.appendChild(nextChildren[i]);
        }
    } else {
        for (i = nextLength; i < currentLength; i++) {
            parent.removeChild(currentChildren[i]);
        }
    }
}

function createElement(parent, childNodes) {
    let currentChildren = [];
    childNodes.forEach(function (ChildNode, idx) {
        currentChildren.push([]);

        ChildNode.forEach(function ($args) {
            updateChildren(parent, currentChildren[idx], $args);
            currentChildren[idx] = $args;
        });
    });

    return util.constantObservable([parent]);
}

module.exports = {
    createElement: createElement,
    textNode: textNode,
    textObservable: textObservable,
    ifStatement: ifStatement
};
