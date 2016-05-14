vistula.dom.createElement("form", [
    ["id", vistula.constantObservable("formId")]
], [
    vistula.dom.textNode("\n    "),
    vistula.dom.createElement("input", [
        ["name", vistula.constantObservable("title")],
        ["placeholder", vistula.constantObservable("Some text")],
        ["value", X.rxFlatMap($arg => ($arg.Z))]
    ], []),
    vistula.dom.textNode("\n")
])
