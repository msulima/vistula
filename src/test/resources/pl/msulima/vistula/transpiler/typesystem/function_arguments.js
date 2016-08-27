function a(X) {
    return X.wrap(1);
};
function b(X) {
    return X.rxFlatMap($arg => ($arg.wrap(1)));
};
function c(X) {
    return 2;
};
function d(X) {
    return 2;
};
c(1);
d(vistula.constantObservable(1));
