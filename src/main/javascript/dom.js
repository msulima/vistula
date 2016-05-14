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

    attributes.forEach(attributeAndValue => {
        let attribute = attributeAndValue[0];
        let Value = attributeAndValue[1];

        Value.rxForEach(value => {
            parent.setAttribute(attribute, value);
            if (isCheckbox(parent, attribute)) {
                parent.checked = value;
            } else if (isText(parent, attribute)) {
                parent.value = value;
            }
        });

        if (isCheckbox(parent, attribute)) {
            parent.addEventListener("change", ev => {
                Value.rxPush(ev.target.checked);
            });
        } else if (isText(parent, attribute)) {
            parent.addEventListener("change", ev => {
                Value.rxPush(ev.target.value);
            });
        }
    });

    let currentChildren = [];
    childNodes.forEach((ChildNode, idx) => {
        currentChildren.push([]);

        ChildNode.rxForEach($args => {
            updateChildren(parent, currentChildren[idx], $args);
            currentChildren[idx] = $args;
        });
    });

    return util.constantObservable([parent]);
}

function isCheckbox(parent, attribute) {
    // TODO what if type is not set yet?
    return parent.nodeName === "INPUT" && parent.type === "checkbox" && attribute === "checked";
}

function isText(parent, attribute) {
    // TODO what if type is not set yet?
    return parent.nodeName === "INPUT" && parent.type === "text" && attribute === "value";
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
