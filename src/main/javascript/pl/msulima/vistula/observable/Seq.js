"use strict";

const ObservableImpl = require("./observable").ObservableImpl;
const constantObservable = require("./constantObservable");
const zip = require("./zip");

function ArrayBuffer(elements) {
    this.elements = elements;

    this.append = constantObservable.constantObservable(Elem => {
        var copy = [].concat(this.elements);
        copy.push(Elem);
        return constantObservable.constantObservable(new ArrayBuffer(copy));
    });

    this.diff = constantObservable.constantObservable(Other => {
        const Obs = new ObservableImpl();

        Other.rxForEachOnce(other => {
            const validElements = this.elements.filter(x => other.elements.indexOf(x) < 0); // FIXME what if pointers?

            Obs.rxPush(new ArrayBuffer(validElements));
        });

        return Obs;
    });

    this.size = constantObservable.constantObservable(() => {
        return constantObservable.constantObservable(this.elements.length);
    });

    this.filter = predicate => {
        return zip.zipAndFlatten(this.elements.map(Dest => {
            return predicate(Dest).rxMap($result => {
                if ($result) {
                    return [Dest];
                } else {
                    return [];
                }
            });
        })).rxMap(x => new ArrayBuffer(x));
    };

    this.map = constantObservable.constantObservable(transformer => {
        const elements2 = this.elements.map(transformer);
        return constantObservable.constantObservable(new ArrayBuffer(elements2));
    });

    this.toArray = constantObservable.constantObservable(() => {
        return constantObservable.constantObservable(this.elements);
    });
}

function Seq(...elements) {
    return constantObservable.constantObservable(new ArrayBuffer(elements))
}

module.exports = {
    ArrayBuffer: ArrayBuffer,
    apply: Seq
};
