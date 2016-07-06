package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Tokenizer, _}


object BinOp {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.BinOp(x, op, y) =>
      Operation(BinOp(op), Seq(Tokenizer.apply(x), Tokenizer.apply(y)))
  }
}

case class BinOp(operator: Ast.operator) extends Operator {

  private val op = operator match {
    case Ast.operator.Add => "+"
    case Ast.operator.Sub => "-"
    case Ast.operator.Mult => "*"
    case Ast.operator.Div => "/"
    case Ast.operator.Mod => "%"
  }

  override def apply(operands: List[Constant]): Constant = {
    operands match {
      case left :: right :: Nil =>
        Constant(s"${left.value} $op ${right.value}")
    }
  }
}
