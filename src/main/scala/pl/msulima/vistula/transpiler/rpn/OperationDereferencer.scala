package pl.msulima.vistula.transpiler.rpn

object OperationDereferencer {

  def apply(operation: Operation): Token = {
    val (op, observables) = ExtractObservables(operation)

    dereference(op, observables)
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
