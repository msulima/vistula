vistula.zipAndFlatten([
    vistula.dom.createElement("span", [], [
        vistula.dom.textNode("\n    hello\n    is it "),
        vistula.dom.textObservable(Lionel.rxMap(function ($arg) {
            return $arg + "?";
        })),
        vistula.dom.textNode("\n    "),
        vistula.dom.createElement("strong", [], [
            vistula.dom.textNode("you lookin'")
        ]),
        vistula.dom.textNode("\n")
    ]),
    vistula.dom.textNode("\n"),
    vistula.dom.createElement("p", [], [
        vistula.dom.textNode("for?")
    ])
])
