const stdlib = vistula.toObservable({
    dom: {
        appendChild: appendChild
    },
    net: {
        ajaxGet: ajaxGet
    }
});

function arrayPush(Dest, Elem) {
    return Dest.rxMap(dest => {
        var copy = [].concat(dest);
        copy.push(Elem);
        return copy;
    });
}

function arrayRemove(Dest, Elem) {
    return vistula.zip([Dest, Elem]).rxMap($args => {
        const dest = $args[0];
        const elem = $args[1];

        return dest.filter(x => x.lastValue != elem); // FIXME bit hacky
    });
}

function arraySize(Dest) {
    return Dest.rxMap(function ($args) {
        return $args.length;
    });
}

function arrayFilter(Dests, predicate) {
    return Dests.rxFlatMap(dests => {
        return vistula.zipAndFlatten(dests.map(Dest => {
            return predicate(Dest).rxMap($result => {
                if ($result) {
                    return [Dest];
                } else {
                    return [];
                }
            });
        }));
    });
}

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
        request.open('GET', url, true);
        request.send(null);

        return obs;
    });
}
