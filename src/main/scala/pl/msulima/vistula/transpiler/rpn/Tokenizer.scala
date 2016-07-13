package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.expression.arithmetic.{BinOp, Compare, UnaryOp}
import pl.msulima.vistula.transpiler.rpn.expression.control._
import pl.msulima.vistula.transpiler.rpn.expression.data.{InlineHtml, InlineJavaScript, Primitives, Tuple}
import pl.msulima.vistula.transpiler.rpn.expression.reference._

object Tokenizer {

  val Ignored = Constant("ignored")

  def boxed(expr: Ast.expr) = {
    Box(apply(expr))
  }

  def boxed(stmt: Ast.stmt) = {
    Box(applyStmt(stmt))
  }

  private val expr: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.Expr(ex) =>
      apply(ex)
  }

  def applyStmt: PartialFunction[Ast.stmt, Token] = {
    expr
      .orElse(Assign.apply)
      .orElse(Declare.apply)
      .orElse(FunctionDef.apply)
      .orElse(If.apply)
      .orElse(Return.apply)
      .orElse(Loop.apply)
  }

  def apply: PartialFunction[Ast.expr, Token] = {
    val priorities =
      InlineHtml.apply
        .orElse(InlineJavaScript.apply)
        .orElse(Primitives.apply)

    priorities
      .orElse(BinOp.apply)
      .orElse(Compare.apply)
      .orElse(Dereference.apply)
      .orElse(FunctionCall.apply)
      .orElse(Generator.apply)
      .orElse(Lambda.apply)
      .orElse(Reference.apply)
      .orElse(Tuple.apply)
      .orElse(UnaryOp.apply)
  }
}
