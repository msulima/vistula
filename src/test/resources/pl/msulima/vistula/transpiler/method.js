W.flatMap(function ($arg) {
    return $arg.X;
}).flatMap(function ($arg) {
    return $arg.Y;
}).flatMap(function ($arg) {
    return $arg(Z);
});
