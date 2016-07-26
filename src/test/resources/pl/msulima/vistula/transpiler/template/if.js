vistula.zipAndFlatten([
    vistula.ifChangedArrays(X.rxMap($arg => ($arg < 3)), [
        vistula.dom.textNode("It's "),
        vistula.dom.textObservable(Y),
        vistula.dom.textNode("\n")
    ], [
        vistula.dom.textNode("3\n")
    ]),
    vistula.dom.textNode("\n\n"),
    vistula.ifChangedArrays(X.rxMap($arg => ($arg < 0)), [
        vistula.dom.textNode("Negative\n")
    ], [])
])