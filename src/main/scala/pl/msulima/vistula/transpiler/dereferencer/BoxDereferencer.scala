package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.{FunctionCall, Reference}
import pl.msulima.vistula.transpiler.scope.Identifier

trait BoxDereferencer {
  this: Dereferencer =>

  def boxDereferencer: PartialFunction[Token, Token] = {
    case Box(token) =>
      dereference(token) match {
        case Observable(t: Constant) =>
          t
        case t: Observable =>
          Operation(Noop, Seq(), t)
        case t =>
          Operation(BoxOp, Seq(), t)
      }
  }

  def boxDereferencer2: PartialFunction[Token, Expression] = {
    case Box(token) =>
      dereference2(token) match {
        case t@ExpressionConstant(_, id: Identifier) if id.observable =>
          t.copy(`type` = id.copy(observable = false))
        case t@ExpressionOperation(_, _, id: Identifier) if id.observable =>
          t.copy(`type` = id.copy(observable = false))
        case t =>
          dereference2(Operation(FunctionCall, Seq(token), Operation(Reference, Seq(Constant("vistula.constantObservable")))))
        // dereference2(FunctionCall("vistula.constantObservable", Seq(token)))
      }
  }
}
