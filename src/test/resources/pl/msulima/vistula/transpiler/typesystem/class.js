function M(x, y) {
    this.x = x;
    this.y = y;
    return vistula.constantObservable(1);
};
const a = new M(1, vistula.constantObservable(2));
a.x + 1;
a.y.rxMap($arg => ($arg + 2));
