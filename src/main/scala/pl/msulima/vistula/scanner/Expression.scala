package pl.msulima.vistula.scanner

import pl.msulima.vistula.Ast
import pl.msulima.vistula.Ast.{expr, stmt}

object Expression {

  def apply: PartialFunction[stmt, Set[String]] = {
    case Ast.stmt.Expr(value) =>
      parseExpression(value)
  }

  lazy val parseExpression: PartialFunction[expr, Set[String]] = {
    case Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Load) => Set(x)
    case Ast.expr.Num(x) => Set()
    case Ast.expr.Str(x) => Set()
    case Ast.expr.BinOp(x, op, y) =>
      parseExpression(x) ++ parseExpression(y)
    case Ast.expr.Compare(left, op +: _, right +: _) =>
      parseExpression(left) ++ parseExpression(right)
    case Ast.expr.Call(func, args, _, _, _) =>
      parseExpression(func) ++ args.flatMap(parseExpression)
  }
}
