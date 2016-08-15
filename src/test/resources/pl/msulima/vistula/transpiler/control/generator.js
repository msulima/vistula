const initial = 42;
const W = vistula.aggregate(Source, initial, function (Acc, Source) {
    return vistula.constantObservable(Acc + Source);
});
