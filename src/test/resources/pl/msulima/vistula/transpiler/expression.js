W;
const Q = vistula.constantObservable(2 + 3);
const X = vistula.constantObservable(2 + 3 + 4);
const Y = a(Y, vistula.constantObservable(3));
const Z = vistula.zip([
    Y.rxMap($arg => ($arg + 3)),
    a(Z.rxMap($arg => ($arg + 1)), vistula.constantObservable(3))
]).rxMap($args => ($args[0] - $args[1]));
