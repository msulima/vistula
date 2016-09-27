package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.Package
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.function.{FunctionDereferencer, ReturnDereferencer}
import pl.msulima.vistula.transpiler.scope.{Scope, ScopeElement}

trait Dereferencer {

  val scope: Scope
  val `package`: Package

  def dereference(token: Token): Expression
}

case class DereferencerImpl(scope: Scope, `package`: Package) extends Dereferencer
  with BoxDereferencer
  with ClassDereferencer
  with DeclareDereferencer
  with DereferenceDereferencer
  with FunctionDereferencer
  with FunctionCallDereferencer
  with ImportDereferencer
  with OperationDereferencer
  with ReferenceDereferencer
  with ReturnDereferencer
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
      ExpressionConstant(x.value, ScopeElement.DefaultConst)
  }
}
