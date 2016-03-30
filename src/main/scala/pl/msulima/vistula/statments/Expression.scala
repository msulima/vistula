package pl.msulima.vistula.statments

import pl.msulima.vistula.Ast.{expr, stmt}
import pl.msulima.vistula.{Ast, Nesting}

object Expression {

  def apply: PartialFunction[stmt, String] = {
    apply(Nesting(Seq(0)))
  }

  def apply(nesting: Nesting): PartialFunction[stmt, String] = {
    case Ast.stmt.Expr(value) =>
      s"$nesting = ${parseExpression(value)}"
  }

  lazy val parseExpression: PartialFunction[expr, String] = {
    case Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Load) => x
    case Ast.expr.Num(x) => x.toString
    case Ast.expr.Str(x) => "\"" + x + "\""
    case Ast.expr.BinOp(x, op, y) =>
      val operator = op match {
        case Ast.operator.Add => "+"
      }
      s"${parseExpression(x)}$operator${parseExpression(y)};"
  }
}
