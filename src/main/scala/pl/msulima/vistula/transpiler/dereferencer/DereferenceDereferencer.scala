package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.Dereference
import pl.msulima.vistula.transpiler.scope.ScopeElement

trait DereferenceDereferencer {
  this: Dereferencer =>

  def dereferenceDereferencer: PartialFunction[Token, Expression] = {
    case Operation(Dereference, target :: Nil, _, _) =>
      val dereferenced = dereference(target)

      dereferenced.`type` match {
        case id: ScopeElement if id.observable =>
          ExpressionOperation(Dereference, Seq(dereferenced), dereferenced.`type`)
        case _ =>
          dereferenced
      }
  }
}
