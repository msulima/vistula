package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast

object Tokenizer extends App {

  def box(expr: Ast.expr): Token = {
    Operation(Box, Seq(apply(expr)))
  }

  def box(token: Token): Token = {
    Operation(Box, Seq(token))
  }

  def apply2(expr: Ast.expr): Token = {
    Dereferencer(apply(expr))
  }

  def apply(expr: Ast.expr): Token = {
    BinOp.apply.orElse(Primitives.apply).orElse(FunctionCall.apply).orElse(Name.apply)(expr)
  }
}
