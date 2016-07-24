package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.{FunctionScope, Return}
import pl.msulima.vistula.transpiler.expression.reference.FunctionCall

trait FunctionDereferencer {
  this: Dereferencer =>

  def functionDereferencer: PartialFunction[Token, Token] = {
    case operation@Operation(FunctionScope, program, _) =>
      val result = Transformer.scoped(program, scope)

      Operation(FunctionScope, result.init :+ Return(result.last), Tokenizer.Ignored)
    case Operation(FunctionCall, arguments, callee) =>
      val dereferencedOutput = dereference(callee)

      val dereferencedInputs = dereferencedOutput match {
        case Observable(t: Constant) =>
          t +: arguments.map(arg => dereference(Box(arg)))
        case _: Observable =>
          dereferencedOutput +: arguments.map(arg => dereference(Box(arg)))
        case _ =>
          dereferencedOutput +: arguments.map(dereference)
      }

      dereferenceOperation(Operation(FunctionCall, dereferencedInputs, dereferencedOutput))
  }
}
