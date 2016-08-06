package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.FunctionDef
import pl.msulima.vistula.transpiler.expression.reference.FunctionCall
import pl.msulima.vistula.transpiler.scope.{FunctionDefinition, ScopeElement}

trait BoxDereferencer {
  this: Dereferencer =>

  def boxDereferencer: PartialFunction[Token, Expression] = {
    case Box(token) =>
      dereference(token) match {
        case t@ExpressionConstant(_, id: ScopeElement) if id.observable =>
          t.copy(`type` = id.copy(observable = false))
        case t@ExpressionOperation(_, _, id: ScopeElement) if id.observable =>
          t.copy(`type` = id.copy(observable = false))
        case t@ExpressionOperation(FunctionDef, _, ScopeElement(false, _: FunctionDefinition)) =>
          t
        case t =>
          dereference(FunctionCall("vistula.constantObservable", Seq(token)))
      }
  }
}
