const A = vistula.constantObservable(2);
const b = 3;
const X = A.rxMap($arg => ($arg + b));
const Y = A.rxLastValue() + b;
const Z = f(A.rxMap($arg => ($arg + 2)), vistula.constantObservable(b));
const W = f(A, vistula.constantObservable(b)).rxLastValue();
