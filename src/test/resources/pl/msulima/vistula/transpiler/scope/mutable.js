const A = vistula.constantObservable(2);
const b = 3;
const X = A.rxMap($arg => ($arg + b));
const Y = A.rxMap($arg => ($arg + b)).rxLastValue();
const Z = f(A.rxMap($arg => ($arg + 2)), vistula.constantObservable(b));
const W = (F(A, vistula.constantObservable(b))).rxLastValue();
const e = 2 + 2;
function F(A, B) {
    const a = A.rxLastValue();
    return a.x();
};
