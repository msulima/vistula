package pl.msulima.vistula.transpiler.rpn

object Dereferencer {

  def apply(token: Token): Token = {
    val foo = FirstStep(token)
    println("1", token)
    println("2", foo)

    foo
    //    val observables = findObservables(token)
    //    println("*", token, "*", observables)
    //    println("")
    //
    //    if (observables.isEmpty) {
    //      substituteObservables(token, Map())
    //    } else if (Seq(token) == observables) {
    //      token match {
    //        case Observable(x) =>
    //          apply(x)
    //      }
    //    } else {
    //      val mapping = createMapping(observables)
    //
    //      val operands = observables.map(x => apply(x.token))
    //      Operation(RxMapOp(observables), operands, substituteObservables(token, mapping))
    //    }
    //  }
    //
    //  private def findObservables(token: Token): Seq[Observable] = {
    //    token match {
    //      case Operation(_, operands, _) =>
    //        operands.flatMap(findObservables)
    //      case x: Observable =>
    //        Seq(x)
    //      case _ =>
    //        Seq()
    //    }
    //  }
    //
    //  private def createMapping(observables: Seq[Observable]): Map[Observable, String] = {
    //    if (observables.size == 1) {
    //      Map(observables.head -> "$arg")
    //    } else {
    //      observables.zipWithIndex.map({
    //        case (mutable, index) => mutable -> s"$$args[$index]"
    //      }).toMap
    //    }
    //  }
    //
    //  private def substituteObservables(token: Token, mapping: Map[Observable, String]): Token = {
    //    token match {
    //      case Operation(operation, operands, output) =>
    //        Operation(operation, operands.map(substituteObservables(_, mapping)), output)
    //      case x: Box =>
    //        rebox(x)
    //      case x: Observable =>
    //        Constant(mapping(x))
    //      case x =>
    //        x
    //    }
    //  }
    //
    //  private def rebox(box: Box): Token = {
    //    box.token match {
    //      case Observable(x) =>
    //        apply(x)
    //      case token =>
    //        apply(token) match {
    //          case op@Operation(RxMapOp(_), _, _) =>
    //            op
    //          case toBox =>
    //            Operation(BoxOp, Seq(), toBox)
    //        }
    //    }
  }
}
