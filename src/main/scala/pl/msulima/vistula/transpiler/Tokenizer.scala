package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.dereferencer.template.TemplateDereferencer
import pl.msulima.vistula.transpiler.expression.Other
import pl.msulima.vistula.transpiler.expression.data.{InlineJavaScript, Primitives, Tuple}
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
      .orElse(Other.apply)
  }

  def apply: PartialFunction[Ast.expr, Token] = {
    val priorities =
      template
        .orElse(InlineJavaScript.apply)
        .orElse(Primitives.apply)

    priorities
      .orElse(Reference.apply)
      .orElse(Tuple.apply)
      .orElse({
        case e: Ast.expr => Direct(Ast.stmt.Expr(e))
      })
  }

  private def template: PartialFunction[Ast.expr, Token] = {
    case expr@Ast.expr.Str(TemplateDereferencer.MagicClasspathHtmlRegex(sourceFile)) =>
      Direct(Ast.stmt.Expr(expr))
    case expr@Ast.expr.Str(x) if x.startsWith(TemplateDereferencer.MagicInlineHtmlPrefix) =>
      Direct(Ast.stmt.Expr(expr))
  }
}
