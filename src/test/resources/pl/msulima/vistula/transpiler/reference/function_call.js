const A = a(Y, vistula.constantObservable(3));
const B = vistula.zip([
    Y,
    a(Z.rxMap($arg => ($arg + 1)), vistula.constantObservable(3))
]).rxMap($args => ($args[0] + 3 - $args[1]));
