vistula.zip([
    Q.flatMap(function ($arg) {
        return $arg.W;
    }),
    X.flatMap(function ($arg) {
        return $arg.Y;
    }).flatMap(function ($arg) {
        return $arg.Z;
    })
]).map(function ($args) {
    return $args[0] + $args[1];
})