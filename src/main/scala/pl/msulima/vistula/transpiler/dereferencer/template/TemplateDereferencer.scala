package pl.msulima.vistula.transpiler.dereferencer.template

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler.dereferencer.control.FunctionDereferencer
import pl.msulima.vistula.transpiler.dereferencer.data.{StaticArray, StaticString}
import pl.msulima.vistula.transpiler.dereferencer.modules.Reference
import pl.msulima.vistula.transpiler.dereferencer.reference.{BoxDereferencer, DeclareDereferencer, FunctionCallDereferencer, LambdaDereferencer}
import pl.msulima.vistula.transpiler.dereferencer.{Dereferencer, DereferencerImpl}
import pl.msulima.vistula.transpiler.scope._
import pl.msulima.vistula.transpiler.{ExpressionOperation, _}

import scala.io.Source

case class Scoped(variables: Seq[Ast.identifier], body: Expression)

object TemplateDereferencer {

  private val MagicInlineHtmlPrefix = "# html\n"
  private val MagicClasspathHtmlRegex = "^# html:(.+?)".r
  val ElementsId = Ast.identifier("$arg")
  val MapFunction = Ast.identifier("map")
  val RxFlatMapFunction = Ast.identifier("rxFlatMap")

  def findVariables: PartialFunction[parser.Node, Seq[Ast.identifier]] = {
    case parser.Element(tag, childNodes) =>
      childNodes.flatMap(findVariables) ++ tag.id.toSeq
    case _ =>
      Seq()
  }
}

trait TemplateDereferencer {
  this: Dereferencer
    with AttributesDereferencer
    with BoxDereferencer
    with DeclareDereferencer
    with FunctionCallDereferencer
    with FunctionDereferencer
    with LambdaDereferencer =>

  private val ZipAndFlatten = Reference(Reference(Scope.VistulaHelper), Ast.identifier("zipAndFlatten"))
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
      functionCall(ZipAndFlatten, Seq(StaticArray(nodes.map(toObservable))))
    }
  }

  def apply(program: Seq[parser.Node]): Seq[Expression] = {
    program.map(applyScope)
  }

  def applyScope(node: parser.Node): Expression = {
    val variables = TemplateDereferencer.findVariables(node)

    val body = apply(node)

    if (variables.isEmpty) {
      body
    } else {
      val variableDeclarations = variables.map(variable => {
        // FIXME constructor and variables are not added to scope
        dereferenceDeclare(IdConstant(variable), Reference(Ast.identifier("new vistula.ObservableImpl()")),
          mutable = true, declare = true)
      })

      val innerBody = variableDeclarations :+ body
      wrap(dereferenceScopeExpr(innerBody))
    }
  }

  private def apply: PartialFunction[parser.Node, Expression] = {
    case parser.Element(tag, childNodes) =>
      val children = childNodes.map(apply)
      val body = StaticArray(children.map(toObservable))

      val tagName = StaticString(tag.name)
      val attribute = dereferenceAttribute(tag)

      tag.id match {
        case Some(id) =>
          val idReference = dereference(IdConstant(id))
          functionCall(CreateBoundElement, Seq(tagName, idReference, attribute, body))
        case None =>
          functionCall(CreateElement, Seq(tagName, attribute, body))
      }
    case parser.ObservableNode(identifier) =>
      functionCall(TextObservable, Seq(dereference(identifier)))
    case parser.IfNode(expr, body, elseBody) =>
      functionCall(IfChangedArrays, Seq(
        dereference(expr),
        StaticArray(apply(body).map(toObservable)),
        StaticArray(apply(elseBody).map(toObservable))
      ))
    case parser.TextNode(text) =>
      functionCall(TextNode, Seq(StaticString(text)))
    case parser.LoopNode(identifier, expression, body) =>
      val inner = {
        val variable = Variable(identifier, ScopeElement.Default)

        val dereferencer = DereferencerImpl(scope.addToScope(variable), `package`)

        val x = dereferencer.functionCall(ZipAndFlatten, Seq(
          StaticArray(apply(body).map(toObservable))
        ))

        val arguments = Seq(variable)

        dereferenceLambda(arguments, dereferenceScopeExpr(Seq(x)))
      }

      val outer = {
        val source = dereference(Reference(TemplateDereferencer.ElementsId))

        val mapFunctionDefinition = FunctionDefinition(Seq(ScopeElement.Default), ScopeElement.DefaultConst)
        val function = ExpressionOperation(Reference, Seq(source, ExpressionConstant(TemplateDereferencer.MapFunction.name, ScopeElement.DefaultConst)), ScopeElement.const(mapFunctionDefinition))

        val x = functionCall(ZipAndFlatten, Seq(
          functionCall(function, mapFunctionDefinition, Seq(inner))
        ))
        val arguments = Seq(Variable(TemplateDereferencer.ElementsId, ScopeElement.DefaultConst))

        dereferenceLambda(arguments, dereferenceScopeExpr(Seq(x)))
      }

      val mapFunctionDefinition = FunctionDefinition(Seq(ScopeElement.Default), ScopeElement.Default)

      val source = {
        val iterable = toObservable(functionCall(Reference(Tokenizer.apply(expression), Ast.identifier("toArray")), Seq()))

        ExpressionOperation(Reference, Seq(iterable, ExpressionConstant(TemplateDereferencer.RxFlatMapFunction.name, ScopeElement.DefaultConst)), ScopeElement.const(mapFunctionDefinition))
      }

      functionCall(source, mapFunctionDefinition, Seq(outer))
  }
}
