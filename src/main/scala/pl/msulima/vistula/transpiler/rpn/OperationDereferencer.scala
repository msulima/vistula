package pl.msulima.vistula.transpiler.rpn

object OperationDereferencer {

  def apply(operation: Operation): Token = {
    val observables = findInputObservables(operation)
    val useFlatMap = operation.output.isInstanceOf[Observable]

    println(operation, observables, useFlatMap)

    if (observables.isEmpty) {
      if (useFlatMap) {
        Observable(operation)
      } else {
        operation
      }
    } else {
      val mapping = createMapping(observables)

      Observable(Operation(RxMapOp(useFlatMap), observables, substituteObservables(operation, mapping)))
    }
  }

  private def findInputObservables(operation: Operation): Seq[Observable] = {
    operation.inputs.collect({
      case input: Observable =>
        input
    })
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

  private def substituteObservables(operation: Operation, mapping: Map[Observable, String]): Token = {
    operation.copy(inputs = operation.inputs.map({
      case input: Observable =>
        Constant(mapping(input))
      case input =>
        input
    }))
  }
}
