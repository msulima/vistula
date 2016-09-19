vistula.dom.createElement("table", [], [
    vistula.dom.textNode("\n    "),
    XS.rxFlatMap($arg => ($arg.toArray)).rxFlatMap($arg => ($arg())).rxFlatMap(function ($arg) {
        return vistula.zipAndFlatten($arg.map(function (X) {
            return vistula.zipAndFlatten([
                vistula.dom.textNode("\n    "),
                vistula.dom.createElement("tr", [], [
                    vistula.dom.textObservable(X)
                ]),
                vistula.dom.textNode("\n    ")
            ]);
        }));
    }),
    vistula.dom.textNode("\n")
])
