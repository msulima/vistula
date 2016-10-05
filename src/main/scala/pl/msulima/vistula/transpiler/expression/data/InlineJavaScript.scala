package pl.msulima.vistula.transpiler.expression.data

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.ScopeElement

object InlineJavaScript {

  private val MagicInlineJavascriptPrefix = "# javascript\n"

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Str(x) if x.startsWith(MagicInlineJavascriptPrefix) =>
      val content = x.stripPrefix(MagicInlineJavascriptPrefix)

      TypedConstant(content, ScopeElement.Unit)
  }
}
