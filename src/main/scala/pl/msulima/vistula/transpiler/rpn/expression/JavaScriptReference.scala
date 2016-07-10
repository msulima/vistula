package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn._

object JavaScriptReference {

  private val MagicInlineJavascriptPrefix = "# javascript\n"

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Str(x) if x.startsWith(MagicInlineJavascriptPrefix) =>
      val content = x.stripPrefix(MagicInlineJavascriptPrefix)

      Operation(Noop, List(), Observable(Constant(content)))
  }
}
