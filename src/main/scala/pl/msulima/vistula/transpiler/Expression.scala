package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Expression {

  def apply: PartialFunction[Ast.stmt, String] = {
    case Ast.stmt.Expr(value) =>
      parseExpression(value)
  }

  lazy val parseExpression: PartialFunction[Ast.expr, String] = {
    case Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Load) => x
    case Ast.expr.Num(x) => x.toString
    case Ast.expr.Str(x) => "\"" + x + "\""
    case Ast.expr.BinOp(x, op, y) =>
      val operator = op match {
        case Ast.operator.Add => "+"
        case Ast.operator.Sub => "-"
      }
      s"${parseExpression(x)} $operator ${parseExpression(y)}"
    case Ast.expr.Compare(x, op +: _, y +: _) =>
      val operator = op match {
        case Ast.cmpop.Lt => "<"
        case Ast.cmpop.Gt => ">"
      }
      s"${parseExpression(x)} $operator ${parseExpression(y)}"
    case Ast.expr.Call(func, args, _, _, _) =>
      Rx.call(parseExpression(func), args.map(parseExpression))
  }
}
