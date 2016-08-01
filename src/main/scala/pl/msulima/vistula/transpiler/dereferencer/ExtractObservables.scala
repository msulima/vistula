package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler.{Observable, Operation, RxMapOp}

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
}
