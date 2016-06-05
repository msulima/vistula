'use strict';

var util = require("./util");

function textNode(text) {
    return util.constantObservable([document.createTextNode(text)]);
}

function textObservable(Obs) {
    const span = document.createElement("span");

    const rxMap = Obs.rxMap($arg => {
        span.textContent = $arg;
        return [span];
    });
    rxMap.marker = "text";
    return rxMap
}

function createBoundElement(tag, Target, attributes, childNodes) {
    const Obs = createJustElement(tag, attributes, childNodes);

    Target.rxPush(Obs.lastValue[0]); // FIXME

    return Obs;
}

function createElement(tag, attributes, childNodes) {
    return createJustElement(tag, attributes, childNodes);
}

function createJustElement(tag, attributes, childNodes) {
    const parent = document.createElement(tag);

    const attributesUnsubscribes = attributes.map(attributeAndValue => {
        const attribute = attributeAndValue[0];
        const Value = attributeAndValue[1];

        if (isEvent(attribute)) {
            setEvent(parent, attribute, Value);
            return () => {
            };
        } else {
            return setAttribute(parent, attribute, Value);
        }
    });

    const currentChildren = [];
    const unsubscribes = childNodes.map((ChildNode, idx) => {
        currentChildren.push([]);

        return ChildNode.rxForEach($args => {
            let offset = 0;
            for (let i = 0; i < idx; i++) {
                offset += currentChildren[i].length;
            }
            updateChildren(parent, offset, currentChildren[idx], $args);
            currentChildren[idx] = $args;
        });
    });

    const Obs = new vistula.ObservableImpl(() => {
        attributesUnsubscribes.forEach(fn => fn());
        unsubscribes.forEach(fn => fn());
    });

    Obs.rxPush([parent]);

    return Obs;
}

function isEvent(attribute) {
    return attribute.startsWith("(");
}

function setEvent(parent, attribute, callback) {
    const eventName = attribute.substring(1, attribute.length - 1);

    parent.addEventListener(eventName, callback);
}

function setAttribute(parent, attribute, Value) {
    if (isCheckbox(parent, attribute)) {
        parent.addEventListener("change", ev => {
            Value.rxPush(ev.target.checked);
        });
    } else if (isText(parent, attribute)) {
        parent.addEventListener("change", ev => {
            Value.rxPush(ev.target.value);
        });
    }

    return Value.rxForEach(value => {
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

}

function isCheckbox(parent, attribute) {
    // TODO what if type is not set yet?
    return parent.nodeName === "INPUT" && parent.type === "checkbox" && attribute === "checked";
}

function isText(parent, attribute) {
    // TODO what if type is not set yet?
    return parent.nodeName === "INPUT" && parent.type === "text" && attribute === "value";
}

function updateChildren(parent, offset, currentChildren, nextChildren) {
    // console.log("A", parent, parent.childNodes, offset, currentChildren, nextChildren);

    const currentLength = currentChildren.length;
    const nextLength = nextChildren.length;

    for (let i = 0; i < Math.min(currentLength, nextLength); i++) {
        parent.replaceChild(nextChildren[i], currentChildren[i]);
    }

    if (currentLength < nextLength) {
        // console.log("C", parent.childNodes);
        for (let i = currentLength; i < nextLength; i++) {
            insertAt(parent, nextChildren[i], offset + i);
        }
    } else {
        for (let i = nextLength; i < currentLength; i++) {
            if (nextChildren.indexOf(currentChildren[i]) < 0) {
                parent.removeChild(currentChildren[i]);
            }
        }
    }

    // console.log("D", parent.childNodes);
}

function insertAt(parent, node, index) {
    if (index >= parent.childNodes.length) {
        parent.appendChild(node);
    } else {
        parent.insertBefore(node, parent.childNodes[index].nextSibling);
    }
}

module.exports = {
    createElement: createElement,
    createBoundElement: createBoundElement,
    textNode: textNode,
    textObservable: textObservable,
    updateChildren: updateChildren
};
