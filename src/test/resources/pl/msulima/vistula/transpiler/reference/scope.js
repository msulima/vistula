const A = vistula.constantObservable(1);
function F() {
    const A = 2;
    return vistula.constantObservable(A + 3);
};
