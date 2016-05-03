package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.Indent

object Loop {

  def apply: PartialFunction[Ast.stmt, String] = {
    case Ast.stmt.For(Ast.expr.Name(Ast.identifier(name), Ast.expr_context.Load), iterExpr, body, _) =>
      val iter = Transpiler(Ast.stmt.Expr(iterExpr))

      val map =
        s"""return $$arg.map(function ($name) {
            |${Indent.leftPad(Transpiler.returnLast(body))}
            |})""".stripMargin

      s"""$iter.rxFlatMap(function ($$arg) {
         |${Indent.leftPad(map)}
         |})""".stripMargin
  }
}