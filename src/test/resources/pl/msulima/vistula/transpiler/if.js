vistula.ifStatement(X.rxMap($arg => ($arg < 3)), vistula.wrap(() => {
    const Y = X.rxMap($arg => ($arg + 3));
    return Y;
}), vistula.constantObservable(3));