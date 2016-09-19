vistula.zipAndFlatten([
    vistula.ifChangedArrays(X.rxMap($arg => ($arg < 3)), [
        vistula.dom.textNode("\nIt's "),
        vistula.dom.textObservable(Y),
        vistula.dom.textNode("\n")
    ], [
        vistula.dom.textNode("\n3\n")
    ]),
    vistula.dom.textNode("\n\n"),
    vistula.ifChangedArrays(X.rxMap($arg => ($arg < 0)), [
        vistula.dom.textNode("\nNegative\n")
    ], []),
    vistula.dom.textNode("\n\n"),
    vistula.ifChangedArrays(X.rxMap($arg => ($arg > 100)), [
        vistula.dom.textNode("\nA\n")
    ], [
        vistula.ifChangedArrays(X.rxMap($arg => ($arg > 10)), [
            vistula.dom.textNode("\nB\n")
        ], [
            vistula.ifChangedArrays(X.rxMap($arg => ($arg > 0)), [
                vistula.dom.textNode("\nC\n")
            ], [
                vistula.dom.textNode("\nD\n")
            ])
        ])
    ])
])
