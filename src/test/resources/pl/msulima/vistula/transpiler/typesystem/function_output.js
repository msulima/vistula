const a = vistula.Seq.apply(vistula.constantObservable(1));
const b = vistula.Seq.apply(vistula.constantObservable(1));
vistula.zip([
    a.rxFlatMap($arg => ($arg.filter)),
    X
]).rxFlatMap($args => ($args[0]($args[1])));
vistula.zip([
    b.rxFlatMap($arg => ($arg.filter)),
    X
]).rxFlatMap($args => ($args[0]($args[1])));
