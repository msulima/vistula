package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast

object Tokenizer {

  def apply(expr: Ast.expr): List[Token] = {
    BinOp.apply.orElse(Primitives.apply).orElse(Name.apply)(expr).toList
  }
}
