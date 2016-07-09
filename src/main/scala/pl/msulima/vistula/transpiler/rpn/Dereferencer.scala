package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.transpiler.Scope

object Dereferencer {
  def apply(scope: Scope)(token: Token): Token = {
    new Dereferencer(scope).apply(token)
  }
}

class Dereferencer(scope: Scope) {
  def apply(token: Token): Token = {
    token match {
      case x: Constant =>
        x
      case Reference(id) =>
        if (scope.variables.contains(id)) {
          Constant(id.name)
        } else {
          Observable(Constant(id.name))
        }
      case Box(x: Box) =>
        apply(x)
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
        val dereferencedInputs = operation.inputs.map(apply)
        val dereferencedOutput = apply(operation.output)

        OperationDereferencer(Operation(operation.operator, dereferencedInputs, dereferencedOutput))
    }
  }
}
