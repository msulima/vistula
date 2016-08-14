function M(x, y) {
    this.x = x;
    this.y = y;
};
const a = new M(1, vistula.constantObservable(2));
a.x + 1;
a.y.rxMap($arg => ($arg + 2));
const b = vistula.constantObservable(new M(1, vistula.constantObservable(2)));
b.rxMap($arg => ($arg.x + 1));
b.rxFlatMap($arg => ($arg.y)).rxMap($arg => ($arg + 2));
