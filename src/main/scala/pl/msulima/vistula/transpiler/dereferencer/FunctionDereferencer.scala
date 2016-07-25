package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.{FunctionScope, Return}
import pl.msulima.vistula.transpiler.expression.reference.FunctionCall

trait FunctionDereferencer {
  this: Dereferencer =>

  def functionDereferencer: PartialFunction[Token, Token] = {
    case operation@Operation(FunctionScope, program, _) =>
      val result = Transformer.scoped(program, scope)

      Operation(FunctionScope, result.init :+ Return(result.last))
    case Operation(FunctionCall, arguments, func) =>
      val dereferencedFunc = dereference(func)

      val dereferencedInputs = dereferencedFunc match {
        case Observable(t: Constant) =>
          t +: arguments.map(arg => dereference(Box(arg)))
        case _: Observable =>
          dereferencedFunc +: arguments.map(arg => dereference(Box(arg)))
        case _ =>
          dereferencedFunc +: arguments.map(dereference)
      }

      dereferenceOperation(FunctionCall(dereferencedFunc, dereferencedInputs))
  }
}
