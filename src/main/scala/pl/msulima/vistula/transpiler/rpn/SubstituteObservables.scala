package pl.msulima.vistula.transpiler.rpn

object SubstituteObservables {

  def apply(observables: Seq[Observable], operation: Operation): Token = {
    val mapping = createMapping(observables)

    operation.copy(inputs = operation.inputs.map({
      case input: Observable =>
        Constant(mapping(input))
      case operation: Operation =>
        apply(observables, operation)
      case input =>
        input
    }))
  }

  private def createMapping(observables: Seq[Observable]): Map[Observable, String] = {
    if (observables.size == 1) {
      Map(observables.head -> "$arg")
    } else {
      observables.zipWithIndex.map({
        case (mutable, index) => mutable -> s"$$args[$index]"
      }).toMap
    }
  }
}
