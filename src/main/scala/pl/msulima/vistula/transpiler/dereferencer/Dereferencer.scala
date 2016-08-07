package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.{Scope, ScopeElement}

object DereferencerImpl {

  def apply(scope: Scope, token: Token): Expression = {
    new DereferencerImpl(scope).dereference(token)
  }
}

trait Dereferencer {

  val scope: Scope

  def dereference(token: Token): Expression
}

case class DereferencerImpl(scope: Scope) extends Dereferencer
  with BoxDereferencer
  with DeclareDereferencer
  with DereferenceDereferencer
  with FunctionDereferencer
  with FunctionCallDereferencer
  with OperationDereferencer
  with ReferenceDereferencer
  with TupleDereferencer {

  override def dereference(token: Token): Expression = {
    declareDereferencer
      .orElse(boxDereferencer)
      .orElse(dereferenceDereferencer)
      .orElse(functionDereferencer)
      .orElse(functionCallDereferencer)
      .orElse(referenceDereferencer)
      .orElse(tupleDereferencer)
      .orElse(operationDereferencer)
      .orElse(default)
      .apply(token)
  }

  private def default: PartialFunction[Token, Expression] = {
    case x: Constant =>
      ExpressionConstant(x.value, ScopeElement(observable = false))
    case Introduce(variable, body) =>
      copy(scope.addToScope(variable)).dereference(body)
  }
}
