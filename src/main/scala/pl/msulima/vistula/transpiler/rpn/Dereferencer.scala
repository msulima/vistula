package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.transpiler.Scope

object Dereferencer {
  def apply(scope: Scope, token: Token): Token = {
    new Dereferencer(new OperationDereferencer(scope)).apply(token)
  }
}

class Dereferencer(operationDereferencer: OperationDereferencer) {

  def apply(token: Token): Token = {
    token match {
      case x: Constant =>
        x
      case Box(boxToken) =>
        unbox(boxToken)
      case observable: Observable =>
        Observable(apply(observable.token))
      case operation@Operation(FunctionScope, _, _) =>
        operation
      case operation@Operation(WrapScope, _, _) =>
        operation
      case operation: Operation =>
        val dereferencedInputs = operation.inputs.map(apply)
        val dereferencedOutput = apply(operation.output)

        operationDereferencer(Operation(operation.operator, dereferencedInputs, dereferencedOutput))
    }
  }
  private def unbox(token: Token): Token = {
    val inner = apply(token)

    token match {
      case _: Box =>
        inner
      case _ =>
        inner match {
          case Observable(t: Constant) =>
            t
          case t: Observable =>
            Operation(Noop, Seq(), t)
          case t =>
            Operation(BoxOp, Seq(), t)
        }
    }
  }
}
