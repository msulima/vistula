package pl.msulima.vistula.transpiler.dereferencer.data

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.expression.data.Tuple

trait TupleDereferencer {
  this: Dereferencer =>

  def tupleDereferencer: PartialFunction[Token, Expression] = {
    case Operation(Tuple, program :: Nil) =>
      val body = dereference(program)
      if (body.`type`.observable) {
        body
      } else {
        ExpressionOperation(Tuple, body)
      }
  }
}
