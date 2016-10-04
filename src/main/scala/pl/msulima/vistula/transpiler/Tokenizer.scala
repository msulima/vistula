package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.expression.Other
import pl.msulima.vistula.transpiler.expression.arithmetic.{BinOp, BoolOp, Compare, UnaryOp}
import pl.msulima.vistula.transpiler.expression.control._
import pl.msulima.vistula.transpiler.expression.data.{InlineHtml, InlineJavaScript, Primitives, Tuple}
import pl.msulima.vistula.transpiler.expression.reference._

object Tokenizer {

  val Pass = Constant("")

  private val expr: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.Expr(ex) =>
      apply(ex)
  }

  def applyStmt: PartialFunction[Ast.stmt, Token] = {
    expr
      .orElse(Declare.apply)
      .orElse(FunctionDef.apply)
      .orElse(If.apply)
      .orElse(Other.apply)
  }

  def apply: PartialFunction[Ast.expr, Token] = {
    val priorities =
      InlineHtml.apply
        .orElse(InlineJavaScript.apply)
        .orElse(Primitives.apply)

    priorities
      .orElse(BinOp.apply)
      .orElse(Compare.apply)
      .orElse(FunctionCall.apply)
      .orElse(If.applyExpr)
      .orElse(Reference.apply)
      .orElse(Tuple.apply)
      .orElse(BoolOp.apply)
      .orElse(UnaryOp.apply)
      .orElse(Other.applyExpr)
  }
}
