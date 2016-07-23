package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.expression.reference.{Dereference, Reference}

class OperationDereferencer(scope: Scope) {

  def apply(operation: Operation): Token = {
    val (op, observables) = ExtractObservables(operation)

    if (op.operator == Reference) {
      reference(op, observables)
    } else if (op.operator == Dereference) {
      dereference(op, observables)
    } else {
      map(op, observables)
    }
  }

  private def reference(operation: Operation, observables: Seq[Token]): Token = {
    operation.inputs.headOption match {
      case Some(observable: Observable) =>
        map(operation.copy(output = Observable(operation.output)), observables)
      case Some(_) =>
        operation
      case None =>
        reference(operation.output.asInstanceOf[Constant].value)
    }
  }

  private def reference(id: String): Token = {
    if (scope.variables.find(_.id == Ast.identifier(id)).exists(!_.`type`.observable)) {
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
