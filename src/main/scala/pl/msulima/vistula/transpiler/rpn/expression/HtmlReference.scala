package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.transpiler.Template
import pl.msulima.vistula.transpiler.rpn._

import scala.io.Source

object HtmlReference {

  private val MagicInlineHtmlPrefix = "# html\n"
  private val MagicClasspathHtmlRegex = "^# html:(.+?)".r

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Str(MagicClasspathHtmlRegex(sourceFile)) =>
      val stream = getClass.getResourceAsStream(sourceFile)
      require(stream != null, s"$sourceFile not found")
      val source = Source.fromInputStream(stream)
      val lines = source.getLines().toList
      source.close()

      toOperation(Template(lines.mkString("\n")))
    case Ast.expr.Str(x) if x.startsWith(MagicInlineHtmlPrefix) =>
      toOperation(Template(x.stripPrefix(MagicInlineHtmlPrefix)))
  }

  private def toOperation(html: String) = {
    Operation(Noop, List(), Observable(Constant(html)))
  }
}