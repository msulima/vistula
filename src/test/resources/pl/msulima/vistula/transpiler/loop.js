var Y = XS.rxFlatMap(function ($arg) {
    return vistula.zip($arg.map(function (X) {
        return X.rxMap(function ($arg) {
            return $arg + 2;
        });
    }))
});
