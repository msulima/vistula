package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.Identifier

object ExtractObservables {

  def apply(operation: Operation) = {
    val (observables, inputs) = findInputObservables(operation)

    (operation.copy(inputs = inputs), observables)
  }

  private def findInputObservables(operation: Operation) = {
    val xs = operation.inputs.map({
      case Observable(Operation(RxMapOp(false), inputs, output, _)) =>
        (inputs, output)
      case input: Observable =>
        (Seq(input), input)
      case input =>
        (Seq(), input)
    })

    (xs.flatMap(_._1), xs.map(_._2))
  }

  def apply(operation: ExpressionOperation) = {
    val (observables, inputs) = findInputObservables(operation)

    (operation.copy(inputs = inputs), observables)
  }

  private def findInputObservables(operation: ExpressionOperation) = {
    val xs = operation.inputs.map({
      case ExpressionOperation(ExpressionMap(output), inputs, id: Identifier) if !id.observable =>
        (inputs, output)
      case ExpressionOperation(_, input :: Nil, id: Identifier) if id.observable =>
        (Seq(input), input)
      case input =>
        (Seq(), input)
    })

    (xs.flatMap(_._1), xs.map(_._2))
  }
}
