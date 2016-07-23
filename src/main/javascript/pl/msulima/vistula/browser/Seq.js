function ArrayBuffer(elements) {
    this.elements = elements;

    this.append = vistula.constantObservable(Elem => {
        var copy = [].concat(this.elements);
        copy.push(Elem);
        return vistula.constantObservable(new ArrayBuffer(copy));
    });

    this.diff = vistula.constantObservable(Other => {
        const Obs = new vistula.ObservableImpl();

        Other.rxForEachOnce(other => {
            const validElements = this.elements.filter(x => other.elements.indexOf(x) < 0); // FIXME what if pointers?

            Obs.rxPush(new ArrayBuffer(validElements));
        });

        return Obs;
    });

    this.size = vistula.constantObservable(() => {
        return vistula.constantObservable(this.elements.length);
    });

    this.filter = vistula.constantObservable(predicate => {
        return vistula.zipAndFlatten(this.elements.map(Dest => {
            return predicate(Dest).rxMap($result => {
                if ($result) {
                    return [Dest];
                } else {
                    return [];
                }
            });
        })).rxMap(x => new ArrayBuffer(x));
    });

    this.toArray = vistula.constantObservable(() => {
        return vistula.constantObservable(this.elements);
    });
}

function Seq(...elements) {
    return vistula.constantObservable(new ArrayBuffer(elements))
}

module.exports = Seq;
