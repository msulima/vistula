vistula.dom.ifStatement(X.rxMap(function ($arg) {
    return $arg < 3;
}), [
    vistula.dom.textNode("It's "),
    vistula.dom.textObservable(Y),
    vistula.dom.textNode("\n")
], [
    vistula.dom.textNode("3\n")
])
