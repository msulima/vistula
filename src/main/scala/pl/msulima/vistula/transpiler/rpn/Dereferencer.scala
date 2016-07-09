package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.transpiler.Scope

object Dereferencer {

  def apply(scope: Scope)(token: Token): Token = {
    token match {
      case x: Constant =>
        x
      case Reference(id) =>
        if (scope.variables.contains(id)) {
          Constant(id.name)
        } else {
          Observable(Constant(id.name))
        }
      case x: Box =>
        apply(scope)(x.token) match {
          case t: Observable =>
            Operation(Noop, Seq(), t)
          case t =>
            Operation(BoxOp, Seq(), t)
        }
      case observable: Observable =>
        Observable(apply(scope)(observable.token))
      case operation: Operation =>
        val dereferencedInputs = operation.inputs.map(apply(scope))
        val dereferencedOutput = apply(scope)(operation.output)

        OperationDereferencer(Operation(operation.operator, dereferencedInputs, dereferencedOutput))
    }
  }
}
