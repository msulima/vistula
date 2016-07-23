const A = vistula.Seq.apply(vistula.constantObservable(1), vistula.constantObservable("A\"B"), vistula.constantObservable({
    "C": vistula.constantObservable("D"),
    "E": vistula.constantObservable(false),
    "F": vistula.constantObservable(2 + 2),
    "G": vistula.constantObservable((3))
}), vistula.constantObservable(null));
