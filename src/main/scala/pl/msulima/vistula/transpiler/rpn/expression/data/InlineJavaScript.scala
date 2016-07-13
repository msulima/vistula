package pl.msulima.vistula.transpiler.rpn.expression.data

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn._

object InlineJavaScript {

  private val MagicInlineJavascriptPrefix = "# javascript\n"

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Str(x) if x.startsWith(MagicInlineJavascriptPrefix) =>
      val content = x.stripPrefix(MagicInlineJavascriptPrefix)

      Operation(Noop, Seq(), Observable(Constant(content)))
  }
}
