'use strict';

var util = require("./util");

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

function createBoundElement(tag, Target, attributes, childNodes) {
    const source = createJustElement(tag, attributes, childNodes);
    Target.rxPush(source);

    return util.constantObservable([source]);
}

function createElement(tag, attributes, childNodes) {
    return util.constantObservable([createJustElement(tag, attributes, childNodes)]);
}

function createJustElement(tag, attributes, childNodes) {
    const parent = document.createElement(tag);

    attributes.forEach(attributeAndValue => {
        const attribute = attributeAndValue[0];
        const argument = attributeAndValue[1];

        if (isEvent(attribute)) {
            setEvent(parent, attribute, argument);
        } else {
            setAttribute(parent, attribute, argument);
        }
    });

    const currentChildren = [];
    childNodes.forEach((ChildNode, idx) => {
        currentChildren.push([]);

        ChildNode.rxForEach($args => {
            updateChildren(parent, currentChildren[idx], $args);
            currentChildren[idx] = $args;
        });
    });

    return parent;
}

function isEvent(attribute) {
    return attribute.startsWith("(");
}

function setEvent(parent, attribute, callback) {
    const eventName = attribute.substring(1, attribute.length - 1);

    parent.addEventListener(eventName, callback);
}

function setAttribute(parent, attribute, Value) {
    Value.rxForEach(value => {
        if (value == null) {
            parent[attribute] = true;
        } else {
            parent.setAttribute(attribute, value);
            if (isCheckbox(parent, attribute)) {
                parent.checked = value;
            } else if (isText(parent, attribute)) {
                parent.value = value;
            }
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
    createBoundElement: createBoundElement,
    textNode: textNode,
    textObservable: textObservable,
    updateChildren: updateChildren
};
