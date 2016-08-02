package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.{Identifier, Scope}

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
  with FunctionDereferencer
  with BoxDereferencer
  with ReferenceDereferencer
  with OperationDereferencer {

  def apply(token: Token): Expression = {
    functionDereferencer
      .orElse(boxDereferencer)
      .orElse(referenceDereferencer)
      .orElse(operationDereferencer)
      .orElse(default)
      .apply(token)
  }

  private def default: PartialFunction[Token, Expression] = {
    case x: Constant =>
      ExpressionConstant(x.value, Identifier(observable = false))
    case Introduce(variable, body) =>
      copy(scope.addToScope(variable)).apply(body)
    case observable: Observable =>
      apply(observable.token)
    //      ExpressionConstant(x.value, Identifier(observable = true))
    //    case observable: Observable =>
    //      Observable(apply(observable.token))
    //    case operation@Operation(WrapScope, _, _, _) =>
    //      operation
  }

  override def dereference(token: Token): Expression = apply(token)
}
