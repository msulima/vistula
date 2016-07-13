package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn._

case object Dereference extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Dereference(value) =>
      Operation(Dereference, Seq(), Tokenizer.apply(value))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"${output.value}.rxLastValue()")
  }
}
