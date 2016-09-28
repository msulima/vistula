const a = vistula.constantObservable(new examples.modules.Bar());
a.rxMap($arg => ($arg.bar()));
