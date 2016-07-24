const W = vistula.aggregate(X, Z, function (Y, Z) {
    return vistula.constantObservable(Y + Z);
});
