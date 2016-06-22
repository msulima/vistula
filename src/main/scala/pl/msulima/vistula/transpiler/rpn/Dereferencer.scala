package pl.msulima.vistula.transpiler.rpn

object Dereferencer {

  def apply(token: Token): Token = {
    val observables = findObservableReferences(token)

    if (observables.isEmpty) {
      token
    } else {
      replaceObservables(token, observables)
    }
  }

  private def findObservableReferences(token: Token): Seq[Reference] = {
    token match {
      case x: Reference =>
        Seq(x)
      case Operation(Box, _) =>
        Seq()
      case Operation(_, operands) =>
        operands.flatMap(findObservableReferences)
      case _ =>
        Seq()
    }
  }

  private def replaceObservables(token: Token, observables: Seq[Reference]): Token = {
    val mapping = if (observables.size == 1) {
      Map(observables.head -> "$arg")
    } else {
      observables.zipWithIndex.map({
        case (mutable, index) => mutable -> s"$$args[$index]"
      }).toMap
    }

    Operation(RxMap(observables), Seq(replaceObservables(token, mapping)))
  }

  private def replaceObservables(token: Token, mapping: Map[Reference, String]): Token = {
    token match {
      case x: Reference =>
        Constant(mapping(x))
      case Operation(operation, operands) =>
        Operation(operation, operands.map(replaceObservables(_, mapping)))
      case x =>
        x
    }
  }
}
