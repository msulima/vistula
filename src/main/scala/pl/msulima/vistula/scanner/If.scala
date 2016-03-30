package pl.msulima.vistula.scanner

import pl.msulima.vistula.Ast.stmt

object If {

  val apply: PartialFunction[stmt, Set[String]] = {
    case stmt.If(test, body, orElse) =>
      Expression.parseExpression(test) ++
        body.flatMap(Statement.apply) ++
        orElse.flatMap(If.apply.orElse(Expression.apply))
  }
}
