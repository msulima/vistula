const a = vistula.constantObservable(new examples.modules.Bar());
a.rxMap($arg => ($arg.bar()));
const b = examples.modules.staticBar(vistula.constantObservable(1));
const c = vistula.constantObservable(new examples.modules.nesting.othernesting.Foo());
c.rxMap($arg => ($arg.foo()));
