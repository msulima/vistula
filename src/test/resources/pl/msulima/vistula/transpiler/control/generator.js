const W = vistula.aggregate(X, Z, ($acc, $source) => {
    const Y = vistula.constantObservable($acc);
    const Z = vistula.constantObservable($source);
    return vistula.zip([
        Y,
        Z
    ]).rxMap($args => ($args[0] + $args[1]));
});
