package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Tokenizer, _}

case object Reference extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Name(id, Ast.expr_context.Load) =>
      Operation(Reference, Seq(), Constant(id.name))
    case Ast.expr.Attribute(expr, id, Ast.expr_context.Load) =>
      Operation(Reference, Seq(Tokenizer.apply(expr)), Observable(Constant(id.name)))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    if (operands.isEmpty) {
      output
    } else {
      Constant(s"${operands.head.value}.${output.value}")
    }
  }
}