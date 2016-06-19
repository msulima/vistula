package pl.msulima.vistula.transpiler.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{CodeTemplate, Static}

object Arithmetic {

  def apply: PartialFunction[Ast.expr, CodeTemplate] = {
    case Ast.expr.UnaryOp(Ast.unaryop.Not, operand) =>
      CodeTemplate("!(%s)", Static, Seq(operand))
    case Ast.expr.BinOp(x, op, y) =>
      val operator = op match {
        case Ast.operator.Add => "+"
        case Ast.operator.Sub => "-"
        case Ast.operator.Mult => "*"
        case Ast.operator.Div => "/"
        case Ast.operator.Mod => "%%" // need to escape String.format
      }

      CodeTemplate(s"%s $operator %s", Static, Seq(x, y))
    case Ast.expr.Compare(x, op +: _, y +: _) =>
      val operator = op match {
        case Ast.cmpop.Lt => "<"
        case Ast.cmpop.LtE => "<="
        case Ast.cmpop.Gt => ">"
        case Ast.cmpop.GtE => ">="
        case Ast.cmpop.Eq => "=="
        case Ast.cmpop.NotEq => "!="
      }

      CodeTemplate(s"%s $operator %s", Static, Seq(x, y))
  }
}
