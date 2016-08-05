package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.Identifier

trait OperationDereferencer {
  this: Dereferencer =>

  def operationDereferencer: PartialFunction[Token, Expression] = {
    case operation: Operation =>
      val (observables, inputs) = extractObservables(operation.inputs)

      val useFlatMap = operation.output.isInstanceOf[Observable]

      val body = ExpressionOperation(operation.operator, inputs, Identifier(observable = useFlatMap))

      if (observables.isEmpty) {
        body
      } else {
        ExpressionOperation(ExpressionMap(body), observables, Identifier(observable = true))
      }
  }


  private def extractObservables(inputs: Seq[Token]): (Seq[Expression], Seq[Expression]) = {
    val xs = inputs.map({
      case x: Box =>
        (Seq(), dereference(x))
      case x =>
        dereference(x) match {
          case ExpressionOperation(ExpressionMap(output@ExpressionOperation(_, _, id: Identifier)), expInputs, _) =>
            (expInputs, output)
          case input@ExpressionOperation(_, _, id: Identifier) if id.observable =>
            (Seq(input), input)
          case input@ExpressionConstant(value, id: Identifier) if id.observable =>
            (Seq(input), input)
          case input =>
            (Seq(), input)
        }
    })

    (xs.flatMap(_._1), xs.map(_._2))
  }
}
