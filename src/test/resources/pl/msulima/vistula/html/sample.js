vistula.dom.createElement(document.createElement("span"), [
    vistula.dom.textNode("hello\n    is it "),
    vistula.dom.textObservable(Lionel),
    vistula.dom.textNode("?\n    "),
    vistula.dom.createElement(document.createElement("strong"), [
        vistula.dom.textNode("you lookin'")
    ])
]);
vistula.dom.createElement(document.createElement("p"), [
    vistula.dom.textNode("for?")
]);
