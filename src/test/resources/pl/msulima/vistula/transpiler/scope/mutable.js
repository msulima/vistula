const A = vistula.constantObservable(2);
const b = 3;
const X = A.rxMap($arg => ($arg + b));
const Y = A.rxLastValue() + b;
