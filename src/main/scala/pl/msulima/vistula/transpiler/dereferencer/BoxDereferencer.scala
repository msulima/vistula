package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.{FunctionCall, Reference}
import pl.msulima.vistula.transpiler.scope.Identifier

trait BoxDereferencer {
  this: Dereferencer =>

  def boxDereferencer: PartialFunction[Token, Expression] = {
    case Box(token) =>
      dereference(token) match {
        case t@ExpressionConstant(_, id: Identifier) if id.observable =>
          t.copy(`type` = id.copy(observable = false))
        case t@ExpressionOperation(_, _, id: Identifier) if id.observable =>
          t.copy(`type` = id.copy(observable = false))
        case t =>
          dereference(Operation(FunctionCall, Seq(token), Operation(Reference, Seq(Constant("vistula.constantObservable")))))
        // dereference(FunctionCall("vistula.constantObservable", Seq(token)))
      }
  }
}
