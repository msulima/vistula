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

function findPositionToInsert(nodes, idx) {
    if (idx == nodes.length - 1 || nodes[idx].length == 0) {
        return null;
    } else {
        return nodes[idx][0];
    }
}

function createElement(parent, childNodes) {
    let currentChildren = [];
    childNodes.forEach(function (ChildNode, idx) {
        currentChildren.push([]);

        ChildNode.forEach(function ($args) {
            currentChildren[idx].forEach(function (child) {
                parent.removeChild(child);
            });
            currentChildren[idx] = $args;

            let positionToInsert = findPositionToInsert(currentChildren, idx);

            $args.forEach(function ($arg) {
                if (positionToInsert == null) {
                    parent.appendChild($arg);
                } else {
                    parent.insertBefore($arg, positionToInsert);
                }
            });
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
