package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Transpiler {

  def apply(expr: Ast.expr): String = {
    apply(Ast.stmt.Expr(expr))
  }

  def apply(stmt: Ast.stmt): String = {
    rpn.Transpiler.toJavaScript(Seq(rpn.Transformer.wrapAndReturnLast(Seq(stmt)))).dropRight(1)
  }
}
