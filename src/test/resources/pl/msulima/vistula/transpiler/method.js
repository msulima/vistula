W.rxFlatMap(function ($arg) {
    return $arg.X;
}).rxFlatMap(function ($arg) {
    return $arg.Y;
}).rxFlatMap(function ($arg) {
    return $arg(Z);
});
