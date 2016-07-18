vistula.zipAndFlatten([
    vistula.dom.createElement("span", [
        [
            "class",
            vistula.constantObservable("foo")
        ],
        [
            "data-foo",
            vistula.constantObservable("bar")
        ],
        [
            "autofocus",
            vistula.constantObservable(null)
        ]
    ], [
        vistula.dom.textNode("\n    hello\n    is it "),
        vistula.dom.textObservable(Lionel.rxMap($arg => ($arg + "?"))),
        vistula.dom.textNode("\n    "),
        vistula.dom.createElement("strong", [], [
            vistula.dom.textNode("you lookin'")
        ]),
        vistula.dom.textNode("\n")
    ]),
    vistula.dom.textNode("\n"),
    vistula.dom.createElement("h3", [], [
        vistula.dom.textNode("for?")
    ])
])
