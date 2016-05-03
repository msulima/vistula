vistula.zipAndFlatten([
    XS.rxFlatMap(function ($arg) {
        return vistula.zipAndFlatten($arg.map(function (X) {
            return vistula.zipAndFlatten([
                vistula.dom.createElement(document.createElement("tr"), [
                    vistula.dom.textObservable(X)
                ]),
                vistula.dom.textNode("\n")
            ]);
        }))
    })
])
