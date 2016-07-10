package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast.identifier
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
        dereference(id)
      case Box(boxToken) =>
        unbox(boxToken)
      case observable: Observable =>
        Observable(apply(observable.token))
      case operation@Operation(Wrap, _, _) =>
        operation
      case operation: Operation =>
        val dereferencedInputs = operation.inputs.map(apply)
        val dereferencedOutput = apply(operation.output)

        OperationDereferencer(Operation(operation.operator, dereferencedInputs, dereferencedOutput))
    }
  }

  private def dereference(id: identifier): Token = {
    if (scope.variables.contains(id)) {
      Constant(id.name)
    } else {
      Observable(Constant(id.name))
    }
  }

  private def unbox(token: Token): Token = {
    val inner = apply(token)

    token match {
      case _: Box =>
        inner
      case _ =>
        inner match {
          case t: Observable =>
            Operation(Noop, Seq(), t)
          case t =>
            Operation(BoxOp, Seq(), t)
        }
    }
  }
}
