vistula.dom.createElement("span", [
    [
        "(click)",
        function (ev) {
            return hello(vistula.constantObservable(ev));
        }
    ]
], [])
