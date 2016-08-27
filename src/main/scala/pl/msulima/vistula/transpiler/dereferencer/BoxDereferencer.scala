package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.FunctionDef
import pl.msulima.vistula.transpiler.expression.reference.Reference
import pl.msulima.vistula.transpiler.scope.{FunctionDefinition, ScopeElement}

trait BoxDereferencer {
  this: Dereferencer with FunctionCallDereferencer =>

  def boxDereferencer: PartialFunction[Token, Expression] = {
    case Observable(token) =>
      dereference(token) match {
        case c: ExpressionConstant =>
          c.copy(`type` = c.`type`.copy(observable = true))
        case c: ExpressionOperation =>
          c.copy(`type` = c.`type`.copy(observable = true))
      }
    case Box(token) =>
      dereference(token) match {
        case c@ExpressionOperation(_, _, id) if id.observable =>
          c.copy(`type` = c.`type`.copy(observable = false))
        case c@ExpressionOperation(_: FunctionDef, _, ScopeElement(false, _: FunctionDefinition)) =>
          c
        case c =>
          toObservable(c)
      }
  }

  def toObservable(expression: Expression) = {
    if (expression.`type`.observable) {
      expression
    } else {
      val function = dereference(Reference("vistula.constantObservable"))
      val functionDefinition = function.`type`.`type`.asInstanceOf[FunctionDefinition]
      functionCall(function, functionDefinition, Seq(expression))
    }
  }
}
