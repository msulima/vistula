vistula.zip([
    Q.rxFlatMap($arg => ($arg.W)),
    X.rxFlatMap($arg => ($arg.Y)).rxFlatMap($arg => ($arg.Z))
]).rxMap($args => ($args[0] + $args[1]));
