W.rxFlatMap($arg => ($arg.X)).rxFlatMap($arg => ($arg.F)).rxFlatMap($arg => ($arg(Y)));
W.rxFlatMap($arg => ($arg.F)).rxFlatMap($arg => ($arg(Y))).rxFlatMap($arg => ($arg.Z));
const w = 2;
const y = 2;
w.x.f(y);
w.f(y).z;
W.rxFlatMap($arg => ($arg.F)).rxFlatMap($arg => ($arg(X.rxFlatMap($arg => ($arg.Y)))));
W.rxFlatMap($arg => ($arg.F)).rxFlatMap($arg => ($arg(vistula.constantObservable(1))));
