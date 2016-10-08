package pl.msulima.vistula.transpiler.dereferencer.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.expression.reference.Declare
import pl.msulima.vistula.transpiler.scope._
import pl.msulima.vistula.transpiler.{ExpressionOperation, _}

trait DeclareDereferencer {
  this: Dereferencer with BoxDereferencer with DereferenceDereferencer =>

  def declareDereferencer: PartialFunction[Token, Expression] = {
    case Operation(dec: Declare, name :: body :: Nil) =>
      dereferenceDeclare(name, body, dec.mutable, dec.declare)
  }

  def dereferenceIntroduce(variable: Variable, body: Expression): ScopedResult = {
    val ns = scope.addToScope(inferType(variable, body))
    ScopedResult(ns, Seq(body))
  }

  private def inferType(variable: Variable, result: Expression): Variable = {
    if (variable.`type`.`type` == ClassReference.Object) {
      variable.copy(`type` = variable.`type`.copy(`type` = result.`type`.`type`))
    } else {
      variable
    }
  }

  def introduce(identifier: Ast.identifier, body: Token, typedef: ClassType, mutable: Boolean, declare: Boolean = true) = {
    val variable = Variable(identifier, ScopeElement(mutable, typedef))
    val result = dereferenceDeclare(IdConstant(identifier), body, mutable, declare)

    ScopedResult(scope.addToScope(variable), Seq(result))
  }

  def dereferenceDeclare(identifier: Token, body: Token, mutable: Boolean, declare: Boolean): Expression = {
    dereferenceDeclare(dereference(identifier), body, mutable, declare)
  }

  def dereferenceDeclare(identifier: Expression, body: Token, mutable: Boolean, declare: Boolean): Expression = {
    val value = dereference(body)

    val dereferencedBody = if (mutable) {
      toObservable(value)
    } else {
      toConstant(value)
    }

    ExpressionOperation(Declare(declare, mutable), Seq(identifier, dereferencedBody), value.`type`)
  }
}
