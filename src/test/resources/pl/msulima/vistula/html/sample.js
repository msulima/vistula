vistula.dom.createElement(document.createElement("span"), [
    document.createTextNode("hello\n    is it "),
    vistula.dom.textObservable(lionel),
    document.createTextNode("?\n    "),
    vistula.dom.createElement(document.createElement("strong"), [
        document.createTextNode("you lookin'")
    ])
]);
vistula.dom.createElement(document.createElement("p"), [
    document.createTextNode("for?")
]);
