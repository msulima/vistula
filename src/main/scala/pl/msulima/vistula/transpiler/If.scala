package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.Indent

object If {

  def apply: PartialFunction[Ast.stmt, String] = {
    case Ast.stmt.If(testExpr, body, orElse) =>
      val test = Transpiler.apply(Ast.stmt.Expr(testExpr))

      s"""$test.flatMap(function ($$ifCondition) {
         |${Indent.leftPad(transpileBody(body, orElse))}
         |})""".stripMargin
  }

  private def transpileBody(body: Seq[Ast.stmt], other: Seq[Ast.stmt]) = {
    s"""if ($$ifCondition) {
        |${Indent.leftPad(Transpiler.returnLast(body))}
        |} else {
        |${Indent.leftPad(Transpiler.returnLast(other))}
        |}""".stripMargin
  }
}
