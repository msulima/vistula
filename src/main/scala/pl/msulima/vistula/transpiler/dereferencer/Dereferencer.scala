package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.Scope

object DereferencerImpl {

  def apply(scope: Scope, token: Token): Token = {
    new DereferencerImpl(scope).apply(token)
  }
}

trait Dereferencer {

  val scope: Scope

  def dereference(token: Token): Token

  def dereferenceOperation(operation: Operation): Token
}

case class DereferencerImpl(scope: Scope) extends Dereferencer
  with FunctionDereferencer
  with BoxDereferencer {

  private val operationDereferencer = new OperationDereferencer(scope)

  def apply(token: Token): Token = {
    functionDereferencer
      .orElse(boxDereferencer)
      .orElse(default).apply(token)
  }

  private def default: PartialFunction[Token, Token] = {
    case x: Constant =>
      x
    case Introduce(variable, body) =>
      copy(scope.addToScope(variable)).apply(body)
    case observable: Observable =>
      Observable(apply(observable.token))
    case operation@Operation(WrapScope, _, _, _) =>
      operation
    case operation: Operation =>
      val dereferencedInputs = operation.inputs.map(apply)
      val dereferencedOutput = apply(operation.output)

      operationDereferencer(Operation(operation.operator, dereferencedInputs, dereferencedOutput))
  }

  override def dereference(token: Token): Token = apply(token)

  override def dereferenceOperation(operation: Operation): Token = operationDereferencer(operation)
}
