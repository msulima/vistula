package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.Scope
import pl.msulima.vistula.transpiler.rpn.expression.reference.{Dereference, Reference}

class OperationDereferencer(scope: Scope) {

  def apply(operation: Operation): Token = {
    val (op, observables) = ExtractObservables(operation)

    // FIXME hacky as fuck
    if (op.operator == Reference) {
      reference(op, observables)
    } else if (op.operator == Dereference) {
      dereference(op, observables)
    } else {
      map(op, observables)
    }
  }

  private def reference(operation: Operation, observables: Seq[Token]): Token = {
    if (operation.inputs.isEmpty) {
      reference(operation.output.asInstanceOf[Constant].value)
    } else {
      operation.inputs.head match {
        case observable: Observable =>
          map(operation, observables)
        case _ =>
          operation.copy(output = operation.output.asInstanceOf[Observable].token)
      }
    }
  }

  private def reference(id: String): Token = {
    if (scope.variables.contains(Ast.identifier(id))) {
      Constant(id)
    } else {
      Observable(Constant(id))
    }
  }

  private def dereference(operation: Operation, observables: Seq[Token]): Token = {
    map(operation, observables) match {
      case Operation(Dereference, Nil, op) =>
        op
      case Observable(op) =>
        op
    }
  }

  private def map(operation: Operation, observables: Seq[Token]) = {
    val useFlatMap = operation.output.isInstanceOf[Observable]

    if (observables.isEmpty) {
      if (useFlatMap) {
        Observable(operation)
      } else {
        operation
      }
    } else {
      Observable(Operation(RxMapOp(useFlatMap), observables, operation))
    }
  }
}
