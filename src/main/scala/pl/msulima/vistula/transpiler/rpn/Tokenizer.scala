package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast

object Tokenizer extends App {

  def apply: PartialFunction[Ast.expr, Token] = {
    BinOp.apply.orElse(Primitives.apply).orElse(FunctionCall.apply).orElse(Name.apply)
  }
}
