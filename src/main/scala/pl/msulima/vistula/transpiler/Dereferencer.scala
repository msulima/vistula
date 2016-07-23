package pl.msulima.vistula.transpiler

import pl.msulima.vistula.transpiler.expression.control.{FunctionScope, Return}
import pl.msulima.vistula.transpiler.expression.reference.FunctionCall

object Dereferencer {
  def apply(scope: Scope, token: Token): Token = {
    new Dereferencer(scope).apply(token)
  }
}

case class Dereferencer(scope: Scope) {

  private val operationDereferencer = new OperationDereferencer(scope)

  def apply(token: Token): Token = {
    token match {
      case x: Constant =>
        x
      case Introduce(variable, body) =>
        copy(scope.addToScope(variable)).apply(body)
      case Box(boxToken) =>
        unbox(boxToken)
      case observable: Observable =>
        Observable(apply(observable.token))
      case operation@Operation(FunctionScope, program, _) =>
        val result = Transformer.scoped(program, scope)

        Operation(FunctionScope, result.init :+ Return(result.last), Tokenizer.Ignored)
      case operation@Operation(WrapScope, _, _) =>
        operation
      case Operation(FunctionCall, arguments, callee) =>
        val dereferencedOutput = apply(callee)

        val dereferencedInputs = dereferencedOutput match {
          case Observable(t: Constant) =>
            t +: arguments.map(arg => apply(Box(arg)))
          case _: Observable =>
            dereferencedOutput +: arguments.map(arg => apply(Box(arg)))
          case _ =>
            dereferencedOutput +: arguments.map(apply)
        }

        operationDereferencer(Operation(FunctionCall, dereferencedInputs, dereferencedOutput))
      case operation: Operation =>
        val dereferencedInputs = operation.inputs.map(apply)
        val dereferencedOutput = apply(operation.output)

        operationDereferencer(Operation(operation.operator, dereferencedInputs, dereferencedOutput))
    }
  }

  private def unbox(token: Token): Token = {
    val inner = apply(token)

    token match {
      case _: Box =>
        inner
      case _ =>
        inner match {
          case Observable(t: Constant) =>
            t
          case t: Observable =>
            Operation(Noop, Seq(), t)
          case t =>
            Operation(BoxOp, Seq(), t)
        }
    }
  }
}
