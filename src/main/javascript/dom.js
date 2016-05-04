'use strict';

var util = require("./util");

function ifStatement(Condition, FragTrue, FragFalse) {
    return util.distinctUntilChanged(Condition).rxFlatMap(function ($condition) {
        let $fragments = $condition ? FragTrue : FragFalse;

        return util.zip($fragments).rxMap(function ($arrays) {
            return [].concat.apply([], $arrays);
        });
    });
}

function textNode(text) {
    return util.constantObservable([document.createTextNode(text)]);
}

function textObservable(Obs) {
    let span = document.createElement("span");
    Obs.rxForEach(function ($arg) {
        span.textContent = $arg;
    });
    return util.constantObservable([span]);
}

function createElement(tag, attributes, childNodes) {
    let parent = document.createElement(tag);

    attributes.forEach(function (attribute) {
        parent.setAttribute(attribute[0], attribute[1]);
    });

    let currentChildren = [];
    childNodes.forEach(function (ChildNode, idx) {
        currentChildren.push([]);

        ChildNode.rxForEach(function ($args) {
            updateChildren(parent, currentChildren[idx], $args);
            currentChildren[idx] = $args;
        });
    });

    return util.constantObservable([parent]);
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

module.exports = {
    createElement: createElement,
    textNode: textNode,
    textObservable: textObservable,
    ifStatement: ifStatement
};
