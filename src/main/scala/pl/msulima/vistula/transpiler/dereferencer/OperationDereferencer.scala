package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.ScopeElement

trait OperationDereferencer {
  this: Dereferencer =>

  def operationDereferencer: PartialFunction[Token, Expression] = {
    case operation: Operation =>
      val (observables, inputs) = extractObservables(operation.inputs)

      val useFlatMap = operation.output.isInstanceOf[Observable]

      val body = ExpressionOperation(operation.operator, inputs, ScopeElement(observable = useFlatMap))

      if (observables.isEmpty) {
        body
      } else {
        ExpressionOperation(ExpressionMap(body), observables, ScopeElement(observable = true))
      }
  }

  private def extractObservables(inputs: Seq[Token]): (Seq[Expression], Seq[Expression]) = {
    val xs = inputs.map({
      case x: Box =>
        (Seq(), dereference(x))
      case x =>
        OperationDereferencer.extractObservables(dereference(x))
    })

    (xs.flatMap(_._1), xs.map(_._2))
  }
}

object OperationDereferencer {

  def extractObservables(expression: Expression): (Seq[Expression], Expression) = {
    expression match {
      case ExpressionOperation(ExpressionMap(output), expInputs, _) =>
        (expInputs, output)
      case input if input.`type`.observable =>
        (Seq(input), input)
      case input =>
        (Seq(), input)
    }
  }
}
