package pl.msulima.vistula.transpiler.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.Fragment

object Arithmetic {

  def apply: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.UnaryOp(Ast.unaryop.Not, operand) =>
      Fragment(Seq(operand), useFlatMap = false) {
        case left :: Nil =>
          s"!($left)"
      }
    case Ast.expr.BinOp(x, op, y) =>
      val operator = op match {
        case Ast.operator.Add => "+"
        case Ast.operator.Sub => "-"
        case Ast.operator.Mult => "*"
        case Ast.operator.Div => "/"
        case Ast.operator.Mod => "%"
      }

      Fragment(Seq(x, y), useFlatMap = false) {
        case left :: right :: Nil =>
          s"$left $operator $right"
      }
    case Ast.expr.Compare(x, op +: _, y +: _) =>
      val operator = op match {
        case Ast.cmpop.Lt => "<"
        case Ast.cmpop.LtE => "<="
        case Ast.cmpop.Gt => ">"
        case Ast.cmpop.GtE => ">="
        case Ast.cmpop.Eq => "=="
        case Ast.cmpop.NotEq => "!="
      }

      Fragment(Seq(x, y), useFlatMap = false) {
        case left :: right :: Nil =>
          s"$left $operator $right"
      }
  }
}
