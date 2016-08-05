package pl.msulima.vistula.transpiler.expression.data

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._

object Tuple extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Tuple(expr +: _, Ast.expr_context.Load) =>
      Operation(Tuple, Seq(Tokenizer.apply(expr)))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"(${operands.head.value})")
  }
}
