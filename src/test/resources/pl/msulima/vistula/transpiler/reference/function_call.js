function F(X, Y) {
    return X;
};
F(X, vistula.constantObservable(3));
F(X, vistula.constantObservable(3)).rxMap($arg => ($arg + 1));
vistula.zip([
    F(X, vistula.constantObservable(3)),
    Y
]).rxMap($args => ($args[0] + $args[1]));
vistula.zip([
    X,
    F(Y.rxMap($arg => ($arg + 1)), vistula.constantObservable(3))
]).rxMap($args => ($args[0] + 3 - $args[1]));
const f = 300;
f(3);
X.rxMap($arg => (f($arg, 3)));
X.rxMap($arg => (f($arg, 3) + 1));
vistula.zip([
    X,
    Y
]).rxMap($args => (f($args[0], 3) + $args[1]));
