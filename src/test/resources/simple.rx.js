var ObservableImpl = function () {
    this.hasValue = false;
    this.lastValue = null;
    this.observers = [];
};

ObservableImpl.prototype.forEach = function (callback) {
    this.observers.push(callback);

    if (this.hasValue) {
        callback(this.lastValue);
    }
};

ObservableImpl.prototype.onNext = function (value) {
    this.hasValue = true;
    this.lastValue = value;

    this.observers.map(function (callback) {
        callback(value);
    });
};

ObservableImpl.prototype.map = function (callback) {
    var observable = new ObservableImpl();

    this.forEach(function (value) {
        observable.onNext(callback(value));
    });

    return observable;
};

ObservableImpl.prototype.flatMap = function (callback) {
    var observable = new ObservableImpl();

    this.forEach(function (next) {
        var nestedObservable = callback(next);

        nestedObservable.forEach(observable.onNext.bind(observable));
    });

    return observable;
};
