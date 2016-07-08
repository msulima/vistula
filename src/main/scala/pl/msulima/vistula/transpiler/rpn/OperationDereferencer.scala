package pl.msulima.vistula.transpiler.rpn

object OperationDereferencer {

  def apply(operation: Operation): Token = {
    val (observables, inputs) = findInputObservables(operation)

    dereference(operation.copy(inputs = inputs), observables)
  }

  private def findInputObservables(operation: Operation) = {
    val xs = operation.inputs.map({
      case input@Observable(Operation(RxMapOp(false), inputs, output)) =>
        (inputs, output)
      case input: Observable =>
        (Seq(input), input)
      case input =>
        (Seq(), input)
    })

    (xs.flatMap(_._1), xs.map(_._2))
  }

  private def dereference(operation: Operation, observables: Seq[Token]) = {
    val useFlatMap = operation.output.isInstanceOf[Observable]

    if (observables.isEmpty) {
      if (useFlatMap) {
        Observable(operation)
      } else {
        operation
      }
    } else {
      Observable(Operation(RxMapOp(useFlatMap), observables, operation))
    }
  }
}
