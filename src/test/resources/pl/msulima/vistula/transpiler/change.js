X.rxMap($arg => ($arg + 2)).rxForEachOnce($arg => A.rxPush($arg));
vistula.constantObservable(false).rxForEachOnce($arg => B.rxPush($arg));
vistula.constantObservable(2 + 3).rxForEachOnce($arg => C.rxFlatMap($arg => ($arg.D)).rxPush($arg));
