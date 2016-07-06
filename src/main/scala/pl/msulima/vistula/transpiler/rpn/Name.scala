package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast

object Name {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Name(id, Ast.expr_context.Load) =>
      Observable(Constant(id.name))
    case Ast.expr.Attribute(expr, id, Ast.expr_context.Load) =>
      Observable(Operation(RxFlatMap, Seq(Box(Tokenizer.apply(expr)), Constant(id.name))))
  }
}
