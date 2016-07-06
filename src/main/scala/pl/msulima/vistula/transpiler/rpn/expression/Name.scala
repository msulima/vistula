package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Box, Tokenizer, _}

object Name {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Name(id, Ast.expr_context.Load) =>
      Observable(Constant(id.name))
    case Ast.expr.Attribute(expr, id, Ast.expr_context.Load) =>
      Observable(Operation(RxFlatMap, Seq(Box(Tokenizer.apply(expr)), Constant(id.name))))
  }
}
