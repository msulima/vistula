package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object If {

  def apply: PartialFunction[Ast.stmt, String] = {
    case Ast.stmt.If(testExpr, body, orElse) =>
      val test = Transpiler.apply(Ast.stmt.Expr(testExpr))

      s"""vistula.ifStatement($test, ${Transpiler.wrap(body)}, ${Transpiler.wrap(orElse)})""".stripMargin
  }
}
