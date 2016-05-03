vistula.zipAndFlatten([
    vistula.dom.createElement(document.createElement("span"), [
        vistula.dom.textNode("\n    hello\n    is it "),
        vistula.dom.textObservable(Lionel.map(function ($arg) {
            return $arg + "?";
        })),
        vistula.dom.textNode("\n    "),
        vistula.dom.createElement(document.createElement("strong"), [
            vistula.dom.textNode("you lookin'")
        ]),
        vistula.dom.textNode("\n")
    ]),
    vistula.dom.textNode("\n"),
    vistula.dom.createElement(document.createElement("p"), [
        vistula.dom.textNode("for?")
    ])
])
