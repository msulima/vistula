vistula.zipAndFlatten([
    vistula.dom.createElement("form", [["id", "task"]], [
        vistula.dom.textNode("\n    "),
        vistula.dom.createElement("input", [["name", "title"], ["placeholder", "What needs to be done?"]], []),
        vistula.dom.textNode("\n")
    ])
])
