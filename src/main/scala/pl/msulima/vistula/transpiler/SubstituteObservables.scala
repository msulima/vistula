package pl.msulima.vistula.transpiler

import pl.msulima.vistula.transpiler.expression.control.FunctionScope

object SubstituteObservables {

  def apply(operation: ExpressionOperation, observables: Seq[Expression]): Expression = {
    val mapping = createMapping(observables)

    apply(mapping, operation)
  }

  private def apply(mapping: Map[Expression, String], operation: ExpressionOperation): Expression = {
    operation.copy(inputs = operation.inputs.map({
      case input@ExpressionOperation(ExpressionFlatMap(body), operands, id) =>
        mapping.get(input).map(mapped => ExpressionConstant(mapped, id)).getOrElse(input)
      case input@ExpressionOperation(FunctionScope, _, _) =>
        input
      case input@ExpressionConstant(value, id) if id.observable =>
        ExpressionConstant(mapping(input), id)
      case input@ExpressionOperation(_, _, id) if id.observable =>
        ExpressionConstant(mapping(input), id)
      case operation: ExpressionOperation =>
        apply(mapping, operation)
      case input =>
        input
    }))
  }

  private def createMapping(observables: Seq[Expression]): Map[Expression, String] = {
    if (observables.size == 1) {
      Map(observables.head -> "$arg")
    } else {
      observables.zipWithIndex.map({
        case (mutable, index) => mutable -> s"$$args[$index]"
      }).toMap
    }
  }
}
