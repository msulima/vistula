package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Tokenizer, _}

object Name {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Name(id, Ast.expr_context.Load) =>
      Observable(Constant(id.name))
    case Ast.expr.Attribute(expr, id, Ast.expr_context.Load) =>
      Observable(Operation(Attribute, Seq(Tokenizer.apply(expr)), Observable(Constant(id.name))))
  }
}

case object Attribute extends Operator {

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"${operands.head.value}.${output.value}")
  }
}
