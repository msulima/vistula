vistula.wrap(() => {
    const inner = new vistula.ObservableImpl();
    const outer = new vistula.ObservableImpl();
    return vistula.dom.createBoundElement("div", outer, [
        ["data-inner", inner.rxFlatMap($arg => ($arg.dataInner))],
        ["data-outer", vistula.constantObservable("outer")]
    ], [
        vistula.dom.textNode("\n    "),
        vistula.dom.createBoundElement("span", inner, [
            ["data-inner", vistula.constantObservable("inner")],
            ["data-outer", outer.rxFlatMap($arg => ($arg.dataOuter))]
        ], []),
        vistula.dom.textNode("\n    "),
        vistula.dom.createElement("strong", [
            ["data-inner", inner.rxFlatMap($arg => ($arg.dataInner))],
            ["data-outer", outer.rxFlatMap($arg => ($arg.dataOuter))]
        ], []),
        vistula.dom.textNode("\n")
    ]);
})
