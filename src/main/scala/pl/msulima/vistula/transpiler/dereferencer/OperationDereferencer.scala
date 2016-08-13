package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopeElement}

trait OperationDereferencer {
  this: Dereferencer =>

  def operationDereferencer: PartialFunction[Token, Expression] = {
    case operation: Operation =>
      val (observables, inputs) = extractObservables(operation.inputs)

      val body = ExpressionOperation(operation.operator, inputs, ScopeElement(observable = false, ClassReference.Object))

      if (observables.isEmpty) {
        body
      } else {
        ExpressionOperation(RxMap(body), observables, body.`type`.copy(observable = true))
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
      case ExpressionOperation(RxMap(output), expInputs, _) =>
        (expInputs, output)
      case input if input.`type`.observable =>
        (Seq(input), input)
      case input =>
        (Seq(), input)
    }
  }
}
