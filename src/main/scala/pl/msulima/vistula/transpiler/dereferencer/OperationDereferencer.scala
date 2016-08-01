package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.{Dereference, Reference}
import pl.msulima.vistula.transpiler.scope.{Identifier, Scope}

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
      case Some(_: Observable) =>
        Observable(Operation(RxMapOp(useFlatMap = true), observables, operation))
      case Some(Operation(Reference, _, _, id: Identifier)) =>
        val maybeTypedOperation = for {
          target <- getType(id, operation.output.asInstanceOf[Constant])
        } yield operation.copy(`type` = target)

        maybeTypedOperation.getOrElse(operation)
      case Some(Constant(id)) =>
        val maybeTypedOperation = for {
          id <- scope.variables.get(Ast.identifier(id))
          target <- getType(id, operation.output.asInstanceOf[Constant])
        } yield operation.copy(`type` = target)

        maybeTypedOperation.getOrElse(operation)
      case Some(_) =>
        operation
      case None =>
        if (scope.functions.contains(operation.output)) {
          operation.copy(`type` = scope.functions(operation.output))
        } else {
          reference(operation.output.asInstanceOf[Constant].value)
        }
    }
  }

  private def getType(id: Identifier, output: Constant) = {
    val clazz = scope.classes(Constant(id.`type`.name))

    clazz.fields.get(Ast.identifier(output.value))
  }

  private def reference(id: String): Token = {
    if (scope.isKnownStatic(Ast.identifier(id))) {
      Constant(id)
    } else {
      Observable(Constant(id))
    }
  }

  private def dereference(operation: Operation, observables: Seq[Token]): Token = {
    map(operation, observables) match {
      case Operation(Dereference, Nil, op, _) =>
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
