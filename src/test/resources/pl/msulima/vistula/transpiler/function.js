function a(X) {
    return X.rxMap($arg => ($arg + 2));
};
function b(X, Y) {
    const Z = vistula.zip([
        X,
        Y
    ]).rxMap($args => ($args[0] + $args[1]));
    return Z.rxMap($arg => ($arg + 2));
};