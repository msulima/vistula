package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Box, Observable}

object Transpiler {

  def apply(expr: Ast.expr): String = {
    apply(Ast.stmt.Expr(expr))
  }

  def apply(stmt: Ast.stmt): String = {
    val wrapped = rpn.Transformer.wrapAndReturnLast(Seq(stmt))

    val toReturn = wrapped match {
      case _: Observable =>
        wrapped
      case x =>
        Box(x)
    }
    println(toReturn)

    rpn.Transpiler.toJavaScript(Seq(toReturn)).dropRight(1)
  }
}
