const a = vistula.Seq.apply(vistula.constantObservable(1));
const b = vistula.Seq.apply(vistula.constantObservable(1));
vistula.zip([
    a,
    X
]).rxFlatMap($args => ($args[0].filter($args[1])));
vistula.zip([
    b,
    X
]).rxFlatMap($args => ($args[0].filter($args[1])));
