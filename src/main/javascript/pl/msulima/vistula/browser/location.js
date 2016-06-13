"use strict";

const hash = new vistula.ObservableImpl();
hash.rxPush(window.location.hash.substring(1));

window.addEventListener("hashchange", ev => {
    hash.rxPush(window.location.hash.substring(1));
});

module.exports = {
    hash: hash
};
