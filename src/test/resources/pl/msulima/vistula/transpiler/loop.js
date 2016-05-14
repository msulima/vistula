const Y = XS.rxFlatMap($arg => (vistula.zip($arg.map(X => {
    return X.rxMap($arg => ($arg + 2));
}))));
