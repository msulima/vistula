package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.stmt

object If {

  def apply: PartialFunction[Ast.stmt, ScanResult] = {
    case stmt.If(testExpr, body, orElse) =>

      val test = Expression.parseSingleExpression(testExpr)

      ResultIf(test, Scanner(body), Scanner(orElse))
  }
}
