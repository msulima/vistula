package pl.msulima.vistula.transpiler.dereferencer.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.dereferencer.modules.Reference

trait BoxDereferencer {
  this: Dereferencer with FunctionCallDereferencer =>

  private val ToConstantObservable = Reference(Reference(Ast.identifier("vistula")), Ast.identifier("constantObservable"))

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
        case c =>
          toObservable(c)
      }
  }

  def toObservable(expression: Expression) = {
    if (expression.`type`.observable) {
      expression
    } else {
      functionCall(ToConstantObservable, Seq(expression))
    }
  }
}
