function Zip(observables) {
    var observable = Observable();

    var results = observables.map(function () {
        return {
            hasValue: false,
            lastValue: null
        };
    });

    observables.forEach(function (observable, i) {
        observable.forEach(function (next) {
            var state = results[i];
            state.hasValue = true;
            state.lastValue = next;
            onNext();
        });
    });

    function onNext() {
        var allSet = results.every(function (result) {
            return result.hasValue;
        });

        if (allSet) {
            observable.onNext(results.map(function (result) {
                return result.lastValue;
            }));
        }
    }

    return observable;
}

function Functor(functor, isSingle) {
    return {
        isSingle: isSingle || false,
        apply: functor
    }
}

function Observable(value) {
    var hasValue = typeof(value) !== "undefined";
    var lastValue = hasValue ? value : null;
    var observers = [];

    function forEach(functor) {
        _forEach(Functor(functor));
    }

    function _forEach(functor) {
        if (!(functor.isSingle && hasValue)) {
            observers.push(functor);
        }

        if (hasValue) {
            functor.apply(lastValue);
        }
    }

    function onNext(value) {
        hasValue = true;
        lastValue = value;

        observers.map(function (functor) {
            functor.apply(value);
        });

        observers = observers.filter(function (functor) {
            return !functor.isSingle;
        })
    }

    function map(functor) {
        var observable = Observable();
        forEach(function (next) {
            observable.onNext(functor(next));
        });

        return observable;
    }

    function flatMap(functor) {
        var observable = Observable();
        _forEach(Functor(function (next) {
            var nestedObservable = functor(next);

            nestedObservable.forEach(function (nestedNext) {
                observable.onNext(nestedNext);
            });
        }, false));

        return observable;
    }

    return {
        forEach: forEach,
        flatMap: flatMap,
        map: map,
        onNext: onNext
    }
}
