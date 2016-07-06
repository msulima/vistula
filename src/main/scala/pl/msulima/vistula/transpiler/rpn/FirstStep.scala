package pl.msulima.vistula.transpiler.rpn

object FirstStep {

  def apply(token: Token): Token = {
    token match {
      case x: Constant =>
        x
      case x: Box =>
        apply(x.token) match {
          case t: Observable =>
            Operation(Noop, Seq(), t)
          case t =>
            Operation(BoxOp, Seq(), t)
        }
      case observable: Observable =>
        Observable(apply(observable.token))
      case operation: Operation =>
        OperationDereferencer(operation.copy(inputs = operation.inputs.map(apply)))
    }
  }
}
