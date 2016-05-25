package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.Indent

object If {

  def apply: PartialFunction[Ast.stmt, String] = {
    case Ast.stmt.If(testExpr, body, orElse) =>
      val test = Transpiler.apply(Ast.stmt.Expr(testExpr))
      val transpiled =
        s"""$test,
           |${Transpiler.wrap(body)},
           |${Transpiler.wrap(orElse)}""".stripMargin

      s"""vistula.ifStatement(
          |${Indent.leftPad(transpiled)}
          |)""".stripMargin
  }
}
