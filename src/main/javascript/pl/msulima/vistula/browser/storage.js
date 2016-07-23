"use strict";

function getOrDefault(Key, Default) {
    return Key.rxFlatMap(key => {
        const storage = localStorage[key];
        if (storage === undefined) {
            return Default;
        } else {
            return vistula.toObservable(JSON.parse(storage));
        }
    });
}

function set(Key, Value) {
    return vistula.zip([Key, vistula.fromObservable(Value)]).rxMap($args => {
        const key = $args[0];
        const value = $args[1];

        localStorage[key] = JSON.stringify(value);
        return value;
    });
}

module.exports = {
    getOrDefault: getOrDefault,
    set: set
};
