package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.Identifier

trait OperationDereferencer {
  this: Dereferencer =>

  def operationDereferencer: PartialFunction[Token, Expression] = {
    case operation: Operation =>
      val (observables, inputs) = ExtractObservables(operation.inputs.map(dereference))

      val useFlatMap = operation.output.isInstanceOf[Observable]

      val body = ExpressionOperation(operation.operator, inputs, Identifier(observable = useFlatMap))

      if (observables.isEmpty) {
        body
      } else {
        ExpressionOperation(ExpressionMap(body), observables, Identifier(observable = true))
      }
  }
}
