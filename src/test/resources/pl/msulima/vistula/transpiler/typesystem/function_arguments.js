function a(X) {
    return X.toString();
};
function b(X) {
    return X.rxMap($arg => ($arg.toString()));
};
function c(X) {
    return 2;
};
function d(X) {
    return 2;
};
c(1);
d(vistula.constantObservable(1));
