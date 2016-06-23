package pl.msulima.vistula.transpiler.rpn

object Dereferencer {

  def apply(token: Token): Token = {
    val observables = findObservableReferences(token)

    replaceObservables(token, observables)
  }

  def rebox(box: Box): Token = {
    apply(box.token) match {
      case op@Operation(RxMapOp(_), _) =>
        op
      case op: Reference =>
        op
      case token =>
        Operation(BoxOp, Seq(token))
    }
  }

  private def findObservableReferences(token: Token): Seq[Rx] = {
    token match {
      case Operation(_, operands) =>
        operands.flatMap(findObservableReferences)
      case x: Rx =>
        Seq(x)
      case _: Box | _: Constant | _: Reference =>
        Seq()
    }
  }

  private def replaceObservables(token: Token, observables: Seq[Rx]): Token = {
    if (observables.isEmpty) {
      replaceObservables(token, Map[Rx, String]())
    } else if (Seq(token) == observables) {
      token match {
        case Rx(x) =>
          replaceObservables(x, Map[Rx, String]())
      }
    } else {
      val mapping = if (observables.size == 1) {
        Map(observables.head -> "$arg")
      } else {
        observables.zipWithIndex.map({
          case (mutable, index) => mutable -> s"$$args[$index]"
        }).toMap
      }

      Operation(RxMapOp(observables), observables.map(x => apply(x.token)) :+ replaceObservables(token, mapping))
    }
  }

  private def replaceObservables(token: Token, mapping: Map[Rx, String]): Token = {
    token match {
      case x: Rx =>
        Constant(mapping(x))
      case Operation(operation, operands) =>
        Operation(operation, operands.map(replaceObservables(_, mapping)))
      case x: Box =>
        rebox(x)
      case x =>
        x
    }
  }
}
