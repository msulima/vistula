const A = XS.rxFlatMap($arg => (vistula.zip($arg.map(X => {
    return X.rxMap($arg => ($arg + 2));
}))));
const B = vistula.constantObservable([
    vistula.constantObservable(1),
    vistula.constantObservable(2)
]).rxFlatMap($arg => (vistula.zip($arg.map(X => {
    const Y = X.rxMap($arg => ($arg + 2));
    return Y;
}))));
