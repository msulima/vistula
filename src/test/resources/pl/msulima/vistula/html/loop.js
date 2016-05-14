XS.rxFlatMap(function ($arg) {
    return vistula.zipAndFlatten($arg.map(function (X) {
        return vistula.zipAndFlatten([
            vistula.dom.createElement("tr", [], [
                vistula.dom.textObservable(X)
            ]),
            vistula.dom.textNode("\n")
        ]);
    }))
})
