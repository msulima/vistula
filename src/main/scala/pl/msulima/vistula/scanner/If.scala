package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.stmt

object If {

  def apply: PartialFunction[Ast.stmt, Seq[Variable]] = {
    case stmt.If(test, body, orElse) =>
      Seq.empty // Expression.parseExpression(test) ++ body.flatMap(Statement.apply) ++ orElse.flatMap(If.apply.orElse(Expression.apply))
  }
}
