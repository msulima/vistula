package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.Identifier

object ExtractObservables {

  def apply(inputs: Seq[Expression]): (Seq[Expression], Seq[Expression]) = {
    val xs = inputs.map({
      case ExpressionOperation(ExpressionMap(output), expInputs, id: Identifier) if !id.observable =>
        (expInputs, output)
      case input@ExpressionOperation(_, _, id: Identifier) if id.observable =>
        (Seq(input), input)
      case input@ExpressionConstant(value, id: Identifier) if id.observable =>
        (Seq(input), input)
      case input =>
        (Seq(), input)
    })

    (xs.flatMap(_._1), xs.map(_._2))
  }
}
