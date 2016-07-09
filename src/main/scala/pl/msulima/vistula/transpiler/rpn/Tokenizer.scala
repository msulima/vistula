package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.expression._

object Tokenizer {

  def boxed(expr: Ast.expr) = {
    Box(apply(expr))
  }

  private val expr: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.Expr(ex) =>
      apply(ex)
  }

  def applyStmt: PartialFunction[Ast.stmt, Token] = {
    expr.orElse(Assign.apply)
  }

  def apply: PartialFunction[Ast.expr, Token] = {
    Primitives.apply
      .orElse(BinOp.apply)
      .orElse(Dereference.apply)
      .orElse(FunctionCall.apply)
      .orElse(Lambda.apply)
      .orElse(Name.apply)
      .orElse(Tuple.apply)
  }
}
