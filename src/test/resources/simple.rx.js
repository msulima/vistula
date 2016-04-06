var ObservableImpl = function () {
    this.hasValue = false;
    this.lastValue = null;
    this.observers = [];
};

ObservableImpl.prototype.forEach = function (callback) {
    this.observers.push(callback);

    if (this.hasValue) {
        callback(this.lastValue, this.unsubscribe.bind(this, callback));
    }
};

ObservableImpl.prototype.onNext = function (value) {
    this.hasValue = true;
    this.lastValue = value;
    var _this = this;

    this.observers.map(function (callback) {
        callback(value, _this.unsubscribe.bind(_this, callback));
    });
};

ObservableImpl.prototype.unsubscribe = function (callback) {
    this.observers = this.observers.filter(function (observer) {
        return observer != callback;
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
    var proxy = new ObservableImpl();
    var currentObservable = null;

    this.forEach(function (next) {
        var nestedObservable = callback(next);
        currentObservable = nestedObservable;

        nestedObservable.forEach(function (value, unsubscribeCallback) {
            if (nestedObservable == currentObservable) {
                proxy.onNext(value);
            } else {
                unsubscribeCallback();
            }
        });
    });

    return proxy;
};
