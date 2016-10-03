package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.{Assign, Declare, Dereference}
import pl.msulima.vistula.transpiler.scope._

trait DeclareDereferencer {
  this: Dereferencer =>

  def declareDereferencer: PartialFunction[Token, Expression] = {
    case Operation(Assign, target :: source :: Nil) =>
      val dereferencedTarget = dereference(target)
      ExpressionOperation(Assign, Seq(dereferencedTarget, dereference(source)), dereferencedTarget.`type`)
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
    val result = dereferenceDeclare(Constant(identifier.name), body, mutable, declare)

    ScopedResult(scope.addToScope(variable), Seq(result))
  }

  def dereferenceDeclare(identifier: Token, body: Token, mutable: Boolean, declare: Boolean): Expression = {
    dereferenceDeclare(dereference(identifier), body, mutable, declare)
  }

  def dereferenceDeclare(identifier: Expression, body: Token, mutable: Boolean, declare: Boolean): Expression = {
    val value = if (mutable) {
      Box(body)
    } else {
      Operation(Dereference, Seq(body))
    }
    val dereferencedBody = dereference(value)
    val underlyingType = dereference(body).`type`

    ExpressionOperation(Declare(declare, mutable), Seq(identifier, dereferencedBody), underlyingType)
  }
}
