package pl.msulima.vistula.transpiler.expression.arithmetic

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Tokenizer, _}


case object UnaryOp extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.UnaryOp(Ast.unaryop.Not, operand) =>
      Operation(UnaryOp, Seq(Tokenizer.apply(operand)))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"!(${operands.head.value})")
  }
}
