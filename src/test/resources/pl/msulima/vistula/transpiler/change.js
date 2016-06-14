Q.rxMap($arg => ($arg + 2)).rxForEachOnce($arg => W.rxPush($arg));
vistula.constantObservable(false).rxForEachOnce($arg => X.rxPush($arg));
vistula.constantObservable(2 + 3).rxForEachOnce($arg => Y.rxFlatMap($arg => ($arg.Z)).rxPush($arg));