package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Box, Dereferencer, Observable, Token}

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

    toJavaScript(toReturn)
  }

  def apply(token: Token): String = {
    toJavaScript(Dereferencer(Scope(Seq(), Seq()), token))
  }

  private def toJavaScript(token: Token): String = {
    rpn.Transpiler.toJavaScript(Seq(token)).dropRight(1)
  }
}
