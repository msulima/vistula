function M(x, y) {
    this.x = x;
    this.y = y;
    return this;
};
const a = new M(1, vistula.constantObservable(2));
a.rxFlatMap($arg => ($arg.x));
a.rxFlatMap($arg => ($arg.y));
