package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._

case object Dereference extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Dereference(value) =>
      Operation(Dereference, Seq(Tokenizer.apply(value)))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"${operands.head.value}.rxLastValue()")
  }
}
