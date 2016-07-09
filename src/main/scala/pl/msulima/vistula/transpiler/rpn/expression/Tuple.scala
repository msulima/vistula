package pl.msulima.vistula.transpiler.rpn.expression


import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Token, Tokenizer}

object Tuple {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Tuple(expr +: _, Ast.expr_context.Load) =>
      Tokenizer.apply(expr)
  }
}
