package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.stmt

object If {

  def apply: PartialFunction[Ast.stmt, ScanResult] = {
    case stmt.If(testExpr, body, orElse) =>
      val test = Expression.apply(Ast.stmt.Expr(testExpr))
      val ifCondition = test.copy(variables = test.variables.init :+ test.variables.last.copy(name = Some(Ast.identifier("__ifCondition"))))

      ResultIf(ifCondition, Scanner(body), Scanner(orElse))
  }
}
