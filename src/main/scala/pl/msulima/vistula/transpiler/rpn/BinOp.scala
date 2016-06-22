package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast


object BinOp {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.BinOp(x, op, y) =>
      ConstantOperation(BinOp(op), Seq(Tokenizer(x), Tokenizer(y)))
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

  override def apply(operands: List[ConstantOperand]): ConstantOperand = {
    operands match {
      case left :: right :: Nil =>
        ConstantOperand(s"${left.value} $op ${right.value}")
    }
  }
}
