vistula.zipAndFlatten([
    vistula.dom.createElement("span", [], [
        vistula.dom.textNode("a  "),
        vistula.dom.textObservable(b),
        vistula.dom.textNode("  c")
    ]),
    vistula.dom.textNode("\n"),
    vistula.dom.createElement("span", [], [
        vistula.dom.textNode("e"),
        vistula.dom.textObservable(f),
        vistula.dom.textNode("g")
    ]),
    vistula.dom.textNode("\n"),
    vistula.dom.createElement("div", [
        [
            "id",
            vistula.constantObservable("")
        ]
    ], [
        vistula.dom.textNode("\n    h\n    i\n")
    ])
])
