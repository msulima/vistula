arrayFilter(XS, function (X, Y) {
    return X.rxMap($arg => ($arg + 2));
});