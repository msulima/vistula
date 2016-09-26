const M = function M(x) {
    this.x = x;
};
M.prototype.bar = function bar(y, z) {
    return 1;
};
const a = vistula.constantObservable(new M(1));
a.rxMap($arg => ($arg.bar(2, vistula.constantObservable(3)) + 4));
