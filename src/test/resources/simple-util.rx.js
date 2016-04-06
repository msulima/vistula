function Zip(observables) {
    var observable = new ObservableImpl();

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

function ConstantObservable(value) {
    var observable = new ObservableImpl();
    observable.onNext(value);
    return observable;
}

function DelayedObservable(value, delay) {
    var observable = new ObservableImpl();
    setTimeout(function () {
        observable.onNext(value);
    }, delay);
    return observable;
}
