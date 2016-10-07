package pl.msulima.vistula.transpiler.dereferencer.template

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.transpiler.Template
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.{Direct, Expression, Token}

import scala.io.Source

trait TemplateDereferencer {
  this: Dereferencer =>

  def templateDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.Str(Template.MagicClasspathHtmlRegex(sourceFile)))) =>
      val stream = getClass.getResourceAsStream(sourceFile)
      require(stream != null, s"$sourceFile not found")
      val source = Source.fromInputStream(stream)
      val lines = source.getLines().toList
      source.close()

      dereference(Template(lines.mkString("\n")))
    case Direct(Ast.stmt.Expr(Ast.expr.Str(x))) if x.startsWith(Template.MagicInlineHtmlPrefix) =>
      dereference(Template(x.stripPrefix(Template.MagicInlineHtmlPrefix)))
  }
}
