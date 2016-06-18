package pl.msulima.vistula.template.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Fragment, RxMap}

import scala.io.Source

object Expression {

  private val MagicInlineHtmlPrefix = "# html\n"
  private val MagicClasspathHtmlRegex = "^# html:(.+?)".r

  def apply: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Str(MagicClasspathHtmlRegex(sourceFile)) =>
      val stream = getClass.getResourceAsStream(sourceFile)
      require(stream != null, s"$sourceFile not found")
      val source = Source.fromInputStream(stream)
      val lines = source.getLines().toList
      source.close()

      Fragment(Template(lines.mkString("\n")), RxMap)
    case Ast.expr.Str(x) if x.startsWith(MagicInlineHtmlPrefix) =>
      Fragment(Template(x.stripPrefix(MagicInlineHtmlPrefix)), RxMap)
  }
}
