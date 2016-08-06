package pl.msulima.vistula.transpiler.dereferencer

import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.reference.{Assign, Declare}
import pl.msulima.vistula.transpiler.scope.Identifier

trait DeclareDereferencer {
  this: Dereferencer =>

  def declareDereferencer: PartialFunction[Token, Expression] = {
    case Operation(Assign, target :: source :: Nil, _, _) =>
      ExpressionOperation(Assign, Seq(dereference(target), dereference(source)), Identifier(observable = true))
    case Operation(Declare, name :: body :: Nil, _, _) =>
      ExpressionOperation(Declare, Seq(dereference(name), dereference(body)), Identifier(observable = true))
  }
}
