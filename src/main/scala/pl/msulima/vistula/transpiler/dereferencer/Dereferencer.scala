package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.{Scope, ScopeElement}

object DereferencerImpl {

  def apply(scope: Scope, token: Token): Expression = {
    new DereferencerImpl(scope).apply(token)
  }
}

trait Dereferencer {

  val scope: Scope

  def dereference(token: Token): Expression
}

case class DereferencerImpl(scope: Scope) extends Dereferencer
  with DeclareDereferencer
  with FunctionDereferencer
  with WrapDereferencer
  with BoxDereferencer
  with ReferenceDereferencer
  with DereferenceDereferencer
  with OperationDereferencer {

  def apply(token: Token): Expression = {
    functionDereferencer
      .orElse(declareDereferencer)
      .orElse(wrapDereferencer)
      .orElse(boxDereferencer)
      .orElse(dereferenceDereferencer)
      .orElse(referenceDereferencer)
      .orElse(operationDereferencer)
      .orElse(default)
      .apply(token)
  }

  private def default: PartialFunction[Token, Expression] = {
    case x: Constant =>
      ExpressionConstant(x.value, ScopeElement(observable = false))
    case Introduce(variable, body) =>
      copy(scope.addToScope(variable)).apply(body)
    case observable: Observable =>
      apply(observable.token) match {
        case c: ExpressionConstant => c.copy(`type` = c.`type`.copy(observable = true))
        case c: ExpressionOperation => c.copy(`type` = c.`type`.copy(observable = true))
      }
    //      ExpressionConstant(x.value, ScopeElement(observable = true))
    //    case observable: Observable =>
    //      Observable(apply(observable.token))
    //    case operation@Operation(WrapScope, _, _, _) =>
    //      operation
  }

  override def dereference(token: Token): Expression = apply(token)
}
