package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.stmt

object If {

  val apply: PartialFunction[stmt, Set[String]] = {
    case stmt.If(test, body, orElse) =>
      Set.empty // Expression.parseExpression(test) ++ body.flatMap(Statement.apply) ++ orElse.flatMap(If.apply.orElse(Expression.apply))
  }
}
