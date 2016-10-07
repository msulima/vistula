package pl.msulima.vistula.transpiler.dereferencer.template

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.dereferencer.control.FunctionDereferencer
import pl.msulima.vistula.transpiler.dereferencer.reference.{BoxDereferencer, FunctionCallDereferencer}
import pl.msulima.vistula.transpiler.expression.control.FunctionDef
import pl.msulima.vistula.transpiler.expression.data.{StaticArray, StaticString}
import pl.msulima.vistula.transpiler.expression.reference.{Declare, FunctionCall, Reference}
import pl.msulima.vistula.transpiler.scope._

import scala.io.Source

case class Scoped(variables: Seq[Ast.identifier], body: Expression)

object TemplateDereferencer {

  val MagicInlineHtmlPrefix = "# html\n"
  val MagicClasspathHtmlRegex = "^# html:(.+?)".r
  val ElementsId = Ast.identifier("$arg")

}

trait TemplateDereferencer {
  this: Dereferencer with FunctionCallDereferencer with BoxDereferencer with FunctionDereferencer =>

  private val ZipAndFlatten = Reference(Reference(Scope.VistulaHelper), Ast.identifier("zipAndFlatten"))
  private val Wrap = Reference(Reference(Scope.VistulaHelper), Ast.identifier("wrap"))
  private val IfChangedArrays = Reference(Reference(Scope.VistulaHelper), Ast.identifier("ifChangedArrays"))
  private val Dom = Reference(Reference(Scope.VistulaHelper), Ast.identifier("dom"))
  private val CreateBoundElement = Reference(Dom, Ast.identifier("createBoundElement"))
  private val CreateElement = Reference(Dom, Ast.identifier("createElement"))
  private val TextNode = Reference(Dom, Ast.identifier("textNode"))
  private val TextObservable = Reference(Dom, Ast.identifier("textObservable"))

  def templateDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.Str(TemplateDereferencer.MagicClasspathHtmlRegex(sourceFile)))) =>
      val stream = getClass.getResourceAsStream(sourceFile)
      require(stream != null, s"$sourceFile not found")
      val source = Source.fromInputStream(stream)
      val lines = source.getLines().toList
      source.close()

      dereferenceTemplate(lines.mkString("\n"))
    case Direct(Ast.stmt.Expr(Ast.expr.Str(x))) if x.startsWith(TemplateDereferencer.MagicInlineHtmlPrefix) =>
      dereferenceTemplate(x.stripPrefix(TemplateDereferencer.MagicInlineHtmlPrefix))
  }

  def dereferenceTemplate(program: String): Expression = {
    val nodes = apply(parser.Parser(program))

    if (nodes.size == 1) {
      nodes.head
    } else {
      functionCall(ZipAndFlatten, Seq(StaticArray.expr(nodes.map(toObservable))))
    }
  }

  def apply(program: Seq[parser.Node]): Seq[Expression] = {
    program.map(applyScope)
  }

  def applyScope(node: parser.Node): Expression = {
    val Scoped(variables, body) = applyWithScope(node)

    if (variables.isEmpty) {
      body
    } else {
      val variableDeclarations = variables.map(variable => {
        dereference(Declare.introduce(variable, body = Operation(Reference, Seq(Constant("new vistula.ObservableImpl()"))),
          typedef = ClassReference.Object, mutable = true)) // FIXME
      })

      // FIXME IfDereferencer
      val innerBody = variableDeclarations :+ body
      val func = FunctionDef(FunctionReference.Anonymous, Seq(), Seq())
      val funcDefinition = FunctionDefinition(Seq(), body.`type`)
      val innerFunction = ExpressionOperation(func, innerBody, ScopeElement.const(funcDefinition))

      functionCall(Wrap, Seq(innerFunction))
    }
  }

  private def applyWithScope: PartialFunction[parser.Node, Scoped] = {
    case parser.Element(tag, childNodes) =>
      val children = childNodes.map(applyWithScope)
      val variables = children.flatMap(_.variables)
      val body = StaticArray.expr(children.map(scoped => toObservable(scoped.body)))

      tag.id.map(id => {
        val code = functionCall(CreateBoundElement, Seq(
          dereference(StaticString(tag.name)), dereference(Constant(id.name)), dereference(Attributes(tag)), body
        ))
        Scoped(variables :+ id, code)
      }).getOrElse({
        val code = functionCall(CreateElement, Seq(
          dereference(StaticString(tag.name)), dereference(Attributes(tag)), body
        ))

        Scoped(variables, code)
      })
    case other: parser.Node =>
      Scoped(Seq(), apply(other))
  }

  private def apply: PartialFunction[parser.Node, Expression] = {
    case parser.ObservableNode(identifier) =>
      functionCall(TextObservable, Seq(dereference(identifier)))
    case parser.IfNode(expr, body, elseBody) =>
      functionCall(IfChangedArrays, Seq(
        dereference(expr),
        StaticArray.expr(apply(body).map(toObservable)),
        StaticArray.expr(apply(elseBody).map(toObservable))
      ))
    case parser.TextNode(text) =>
      functionCall(TextNode, Seq(dereference(StaticString(text))))
    case parser.LoopNode(identifier, expression, body) =>
      val iterable = FunctionCall(Reference(
        Tokenizer.apply(expression), Ast.identifier("toArray")
      ), Seq())

      val inner = functionCall(ZipAndFlatten, Seq(StaticArray.expr(apply(body).map(toObservable))))

      val outer = functionCall(Reference(Reference(TemplateDereferencer.ElementsId), Ast.identifier("map")), Seq(inner))

      toObservable(functionCall(Reference(Box(iterable), Ast.identifier("rxFlatMap")), Seq(outer)))
  }
}
