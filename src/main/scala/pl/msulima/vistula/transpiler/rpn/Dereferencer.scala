package pl.msulima.vistula.transpiler.rpn

object Dereferencer {

  def apply(token: Token): Token = {
    val observables = findObservables(token)

    if (observables.isEmpty) {
      substituteObservables(token, Map())
    } else if (Seq(token) == observables) {
      token match {
        case Observable(x) =>
          substituteObservables(x, Map())
      }
    } else {
      val mapping = createMapping(observables)

      val operands = observables.map(x => apply(x.token)) :+ substituteObservables(token, mapping)
      Operation(RxMapOp(observables), operands)
    }
  }

  private def findObservables(token: Token): Seq[Observable] = {
    token match {
      case Operation(_, operands) =>
        operands.flatMap(findObservables)
      case x: Observable =>
        Seq(x)
      case Box(Observable(_)) =>
        Seq()
      case _: Box | _: Constant | _: Reference =>
        Seq()
    }
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

  private def substituteObservables(token: Token, mapping: Map[Observable, String]): Token = {
    token match {
      case Operation(operation, operands) =>
        Operation(operation, operands.map(substituteObservables(_, mapping)))
      case x: Box =>
        rebox(x)
      case x: Observable =>
        Constant(mapping(x))
      case x =>
        x
    }
  }

  private def rebox(box: Box): Token = {
    box.token match {
      case Observable(x) =>
        apply(x)
      case token =>
        apply(token) match {
          case op@Operation(RxMapOp(_), _) =>
            op
          case op: Reference =>
            op
          case toBox =>
            Operation(BoxOp, Seq(toBox))
        }
    }
  }
}
