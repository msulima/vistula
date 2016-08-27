const X = 42;
X.textObservable(vistula.constantObservable("a"));
const a = vistula.Seq.apply(vistula.constantObservable(1));
const b = vistula.Seq.apply(vistula.constantObservable(1));
vistula.zip([
    a,
    Y
]).rxFlatMap($args => ($args[0].filter($args[1])));
vistula.zip([
    b,
    Y
]).rxFlatMap($args => ($args[0].filter($args[1])));
