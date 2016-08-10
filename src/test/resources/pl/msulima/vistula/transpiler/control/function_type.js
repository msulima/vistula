function a(X) {
    return X.wrap(1);
};
function b(X) {
    return X.rxFlatMap($arg => ($arg.wrap)).rxFlatMap($arg => ($arg(vistula.constantObservable(1))));
};
function c(X) {
    return vistula.constantObservable(2);
};
function d(X) {
    return vistula.constantObservable(2);
};
c(1);
d(vistula.constantObservable(1));
