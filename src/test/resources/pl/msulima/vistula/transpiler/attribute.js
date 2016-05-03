vistula.zip([
    Q.rxFlatMap(function ($arg) {
        return $arg.W;
    }),
    X.rxFlatMap(function ($arg) {
        return $arg.Y;
    }).rxFlatMap(function ($arg) {
        return $arg.Z;
    })
]).rxMap(function ($args) {
    return $args[0] + $args[1];
});
