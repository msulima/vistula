const A = XS.rxFlatMap($arg => ($arg.map)).rxFlatMap($arg => ($arg(function (X) {
    return X.rxMap($arg => ($arg + 2));
})));
const B = vistula.Seq.apply(vistula.constantObservable(1), vistula.constantObservable(2)).rxFlatMap($arg => ($arg.map)).rxFlatMap($arg => ($arg(function (X) {
    const Y = X.rxMap($arg => ($arg + 2));
    ;
    return Y;
})));
