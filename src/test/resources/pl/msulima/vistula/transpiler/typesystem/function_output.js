function c(X) {
    return X;
};
function d(X) {
    return X;
};
const a = vistula.constantObservable(c(1) + 2);
const b = d(vistula.constantObservable(1)).rxMap($arg => ($arg + 2));
