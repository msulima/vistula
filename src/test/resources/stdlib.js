let stdlib = vistula.toObservable({
    dom: {
        appendChild: appendChild
    },
    net: {
        ajaxGet: ajaxGet
    }
});

function arrayPush(Dest, Elem) {
    return vistula.zip([Dest, Elem]).rxMap(function ($args) {
        if ($args[0].length == 3) {
            return [];
        }
        var copy = [].concat($args[0]);
        copy.push($args[1]);
        return copy;
    });
}

function appendChild(Target, Observables) {
    return vistula.zip([Target, Observables]).rxMap(function ($args) {
        var target = document.getElementById($args[0]);
        $args[1].forEach(function (obs) {
            target.appendChild(obs);
        });
    });
}

function ajaxGet(Url) {
    var obs = new vistula.ObservableImpl();

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
