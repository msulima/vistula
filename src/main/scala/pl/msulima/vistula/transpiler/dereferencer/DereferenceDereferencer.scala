package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.Dereference

trait DereferenceDereferencer {
  this: Dereferencer =>

  def dereferenceDereferencer: PartialFunction[Token, Expression] = {
    case Operation(Dereference, target :: Nil, _, _) =>
      val dereferenced = dereference(target)

      if (dereferenced.`type`.observable) {
        ExpressionOperation(Dereference, Seq(dereferenced), dereferenced.`type`.copy(observable = false))
      } else {
        dereferenced
      }
  }
}
