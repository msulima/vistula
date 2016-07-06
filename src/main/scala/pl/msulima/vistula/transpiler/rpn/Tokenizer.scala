package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn

object Tokenizer {

  private val expr: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.Expr(ex) =>
      apply(ex)
  }

  def applyStmt: PartialFunction[Ast.stmt, Token] = {
    expr.orElse(rpn.Assign.apply)
  }

  def apply: PartialFunction[Ast.expr, Token] = {
    BinOp.apply.orElse(Primitives.apply).orElse(FunctionCall.apply).orElse(Name.apply).orElse(Dereference.apply)
  }
}
