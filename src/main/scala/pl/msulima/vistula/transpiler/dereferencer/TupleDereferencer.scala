package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.data.Tuple

trait TupleDereferencer {
  this: Dereferencer =>

  def tupleDereferencer: PartialFunction[Token, Expression] = {
    case Operation(Tuple, program :: Nil, _, _) =>
      val body = dereference(program)
      if (body.`type`.observable) {
        body
      } else {
        ExpressionOperation(Tuple, Seq(body), body.`type`)
      }
  }
}
