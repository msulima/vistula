vistula.ifStatement(
    X.rxMap($arg => ($arg < 3)),
    vistula.wrap(() => {
        const Y = X.rxMap($arg => ($arg + 3));
        return Y;
    }),
    vistula.ifStatement(
        X.rxMap($arg => ($arg < 0)).rxMap($arg => (!($arg))),
        X,
        vistula.constantObservable(3)
    )
);