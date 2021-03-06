package pl.msulima.vistula.transpiler

import pl.msulima.vistula.transpiler.dereferencer.control.FunctionScope

object SubstituteObservables {

  def apply(operation: ExpressionOperation, observables: Seq[Expression]): Expression = {
    val mapping = createMapping(observables)

    apply(mapping, operation)
  }

  private def apply(mapping: Map[Expression, String], operation: ExpressionOperation): Expression = {
    operation.copy(inputs = operation.inputs.map({
      case input@ExpressionOperation(FunctionScope, _, _) =>
        input
      case input@ExpressionOperation(_, _, typedef) if typedef.observable =>
        mapping.get(input).map(mapped => ExpressionConstant(mapped, typedef)).getOrElse(input)
      case input@ExpressionConstant(_, typedef) if typedef.observable =>
        mapping.get(input).map(mapped => ExpressionConstant(mapped, typedef)).getOrElse(input)
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
