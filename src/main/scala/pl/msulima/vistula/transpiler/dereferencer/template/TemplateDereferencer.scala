package pl.msulima.vistula.transpiler.dereferencer.template

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.template.transpiler.{Attributes, Template}
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.dereferencer.reference.FunctionCallDereferencer
import pl.msulima.vistula.transpiler.expression.data.{StaticArray, StaticString}
import pl.msulima.vistula.transpiler.expression.reference.Reference
import pl.msulima.vistula.transpiler.scope.{Scope, ScopeElement}
import pl.msulima.vistula.transpiler.{Direct, Expression, ExpressionOperation, Token}

import scala.io.Source

trait TemplateDereferencer {
  this: Dereferencer with FunctionCallDereferencer =>

  private val Dom = Reference(Reference(Scope.VistulaHelper), Ast.identifier("dom"))
  private val CreateElement = Reference(Dom, Ast.identifier("createElement"))
  private val TextNode = Reference(Dom, Ast.identifier("textNode"))
  private val TextObservable = Reference(Dom, Ast.identifier("textObservable"))

  def templateDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.Str(Template.MagicClasspathHtmlRegex(sourceFile)))) =>
      val stream = getClass.getResourceAsStream(sourceFile)
      require(stream != null, s"$sourceFile not found")
      val source = Source.fromInputStream(stream)
      val lines = source.getLines().toList
      source.close()

      dereferenceTemplate(lines.mkString("\n"))
    case Direct(Ast.stmt.Expr(Ast.expr.Str(x))) if x.startsWith(Template.MagicInlineHtmlPrefix) =>
      dereferenceTemplate(x.stripPrefix(Template.MagicInlineHtmlPrefix))
  }

  def dereferenceTemplate(program: String) = {
    val nodes = parser.Parser(program)

    if (nodes.size == 1) {
      dereferenceNode(nodes.head)
    } else {
      dereference(Template(program))
    }
  }

  private def dereferenceNode: PartialFunction[parser.Node, Expression] = {
    case parser.Element(tag, childNodes) =>
      val tagName = dereference(StaticString(tag.name))
      val attributes = dereference(Attributes(tag))
      val body = ExpressionOperation(StaticArray, childNodes.map(dereferenceNode), ScopeElement.DefaultConst)

      functionCall(CreateElement, Seq(tagName, attributes, body))
    case parser.ObservableNode(identifier) =>
      functionCall(TextObservable, Seq(dereference(identifier)))
    case parser.TextNode(text) =>
      functionCall(TextNode, Seq(dereference(StaticString(text))))
  }
}
