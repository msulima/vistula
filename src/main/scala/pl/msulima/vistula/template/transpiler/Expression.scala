package pl.msulima.vistula.template.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{CodeTemplate, RxMap}

import scala.io.Source

object Expression {

  private val MagicInlineHtmlPrefix = "# html\n"
  private val MagicClasspathHtmlRegex = "^# html:(.+?)".r

  def apply: PartialFunction[Ast.expr, CodeTemplate] = {
    case Ast.expr.Str(MagicClasspathHtmlRegex(sourceFile)) =>
      val stream = getClass.getResourceAsStream(sourceFile)
      require(stream != null, s"$sourceFile not found")
      val source = Source.fromInputStream(stream)
      val lines = source.getLines().toList
      source.close()

      CodeTemplate(Template(lines.mkString("\n")), RxMap)
    case Ast.expr.Str(x) if x.startsWith(MagicInlineHtmlPrefix) =>
      CodeTemplate(Template(x.stripPrefix(MagicInlineHtmlPrefix)), RxMap)
  }
}
