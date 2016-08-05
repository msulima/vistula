package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.Return

trait WrapDereferencer {
  this: Dereferencer =>

  def wrapDereferencer: PartialFunction[Token, Expression] = {
    case Operation(WrapScope, program, _, _) =>
      val result = program.map(dereference)

      val body = if (result.isEmpty || result.size == 1) {
        result
      } else {
        result.init :+ Return(result.last)
      }

      ExpressionOperation(WrapScope, body, body.last.`type`)
  }
}
