XS.rxFlatMap(function ($arg) {
    return $arg.map(function (X) {
        return X.rxMap(function ($arg) {
            return $arg + 2;
        });
    })
});
