function b(X) {
    return X.rxFlatMap($arg => ($arg.wrap)).rxFlatMap($arg => ($arg(1)));
};