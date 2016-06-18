package pl.msulima.vistula.transpiler.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Fragment, Static}

object Arithmetic {

  def apply: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.UnaryOp(Ast.unaryop.Not, operand) =>
      Fragment("!(%s)", Static, Seq(operand))
    case Ast.expr.BinOp(x, op, y) =>
      val operator = op match {
        case Ast.operator.Add => "+"
        case Ast.operator.Sub => "-"
        case Ast.operator.Mult => "*"
        case Ast.operator.Div => "/"
        case Ast.operator.Mod => "%%" // need to escape String.format
      }

      Fragment(s"%s $operator %s", Static, Seq(x, y))
    case Ast.expr.Compare(x, op +: _, y +: _) =>
      val operator = op match {
        case Ast.cmpop.Lt => "<"
        case Ast.cmpop.LtE => "<="
        case Ast.cmpop.Gt => ">"
        case Ast.cmpop.GtE => ">="
        case Ast.cmpop.Eq => "=="
        case Ast.cmpop.NotEq => "!="
      }

      Fragment(s"%s $operator %s", Static, Seq(x, y))
  }
}
