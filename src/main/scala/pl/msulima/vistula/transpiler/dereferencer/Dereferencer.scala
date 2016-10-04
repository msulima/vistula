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
  with AssignDereferencer
  with BoxDereferencer
  with ClassDereferencer
  with ConstructorDereferencer
  with DeclareDereferencer
  with DereferenceDereferencer
  with FunctionDereferencer
  with FunctionCallDereferencer
  with ImportDereferencer
  with LoopDereferencer
  with OperationDereferencer
  with ReferenceDereferencer
  with ReturnDereferencer
  with TupleDereferencer {

  override def dereference(token: Token): Expression = {
    declareDereferencer
      .orElse(assignDereferencer)
      .orElse(boxDereferencer)
      .orElse(dereferenceDereferencer)
      .orElse(functionDereferencer)
      .orElse(functionCallDereferencer)
      .orElse(referenceDereferencer)
      .orElse(tupleDereferencer)
      .orElse(loopDereferencer)
      .orElse(operationDereferencer)
      .orElse(default)
      .apply(token)
  }

  private def default: PartialFunction[Token, Expression] = {
    case x: Constant =>
      ExpressionConstant(x.value, ScopeElement.DefaultConst)
    case x: TypedConstant =>
      ExpressionConstant(x.value, x.`type`)
  }
}
