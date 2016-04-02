package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.scanner.FlatVariable

object Expression {

  def apply: PartialFunction[FlatVariable, String] = {
    case FlatVariable(target, value: Ast.expr.Call, dependsOn) =>
      s"var $target = ${parseExpression(value)};"
    case FlatVariable(target, value, dependsOn) =>
      s"var $target = ${Rx.map(dependsOn.map(_.name), parseExpression(value))};"
  }

  private lazy val parseExpression: PartialFunction[Ast.expr, String] = {
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
      s"${parseExpression(func)}(${args.map(parseExpression).mkString(", ")})"
  }
}
