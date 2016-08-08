function a(X) {
    return X.wrap(1);
};
function b(X) {
    return X.rxFlatMap($arg => ($arg.wrap)).rxFlatMap($arg => ($arg(vistula.constantObservable(1))));
};
