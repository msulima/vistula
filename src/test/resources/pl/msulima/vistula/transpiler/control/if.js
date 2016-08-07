vistula.ifStatement(X.rxMap($arg => ($arg < 3)), vistula.wrap(function () {
    const Y = X.rxMap($arg => ($arg + 3));
    return Y;
}), vistula.ifStatement(X.rxMap($arg => (!($arg < 0))), X, vistula.constantObservable(3)));
vistula.ifStatement(vistula.constantObservable(true), X, vistula.constantObservable(1));
