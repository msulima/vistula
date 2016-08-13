package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.{Assign, Declare, Dereference}
import pl.msulima.vistula.transpiler.scope.ScopeElement

trait DeclareDereferencer {
  this: Dereferencer =>

  def declareDereferencer: PartialFunction[Token, Expression] = {
    case Operation(Assign, target :: source :: Nil) =>
      ExpressionOperation(Assign, Seq(dereference(target), dereference(source)), ScopeElement(observable = true))
    case Operation(dec: Declare, name :: body :: Nil) =>
      val value = if (dec.mutable) {
        Box(body)
      } else {
        Operation(Dereference, Seq(body))
      }
      val dereferencedBody = dereference(value)

      ExpressionOperation(dec, Seq(dereference(name), dereferencedBody), dereference(body).`type`)
  }
}
