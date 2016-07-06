package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast

object Dereference extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Dereference(value) =>
      Operation(Dereference, Seq(Box(Tokenizer.apply(value))))
  }

  def apply(operands: List[Constant]): Constant = {
    Constant(s"${operands.head.value}.rxLastValue()")
  }
}
