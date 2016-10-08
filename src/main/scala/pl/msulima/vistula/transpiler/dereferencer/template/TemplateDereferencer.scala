package pl.msulima.vistula.transpiler.dereferencer.template

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler.dereferencer.control.FunctionDereferencer
import pl.msulima.vistula.transpiler.dereferencer.reference.{BoxDereferencer, FunctionCallDereferencer}
import pl.msulima.vistula.transpiler.dereferencer.{Dereferencer, DereferencerImpl}
import pl.msulima.vistula.transpiler.expression.control.FunctionDef2
import pl.msulima.vistula.transpiler.expression.data.{StaticArray, StaticString}
import pl.msulima.vistula.transpiler.expression.reference.{Declare, Reference}
import pl.msulima.vistula.transpiler.scope._
import pl.msulima.vistula.transpiler.{ExpressionOperation, _}

import scala.io.Source

case class Scoped(variables: Seq[Ast.identifier], body: Expression)

object TemplateDereferencer {

  val MagicInlineHtmlPrefix = "# html\n"
  val MagicClasspathHtmlRegex = "^# html:(.+?)".r
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
  this: Dereferencer with FunctionCallDereferencer with BoxDereferencer with FunctionDereferencer =>

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
      functionCall(ZipAndFlatten, Seq(StaticArray.expr(nodes.map(toObservable))))
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
      val variableDeclarations = dereference(variables.map(variable => {
        Declare.introduce(variable, body = Operation(Reference, Seq(Constant("new vistula.ObservableImpl()"))),
          typedef = ClassReference.Object, mutable = true) // FIXME
      }))

      val innerBody = variableDeclarations :+ body
      wrap(dereferenceScopeExpr(innerBody))
    }
  }

  private def apply: PartialFunction[parser.Node, Expression] = {
    case parser.Element(tag, childNodes) =>
      val children = childNodes.map(apply)
      val body = StaticArray.expr(children.map(toObservable))

      tag.id.map(id => {
        functionCall(CreateBoundElement, Seq(
          dereference(StaticString(tag.name)), dereference(Constant(id.name)), dereference(Attributes(tag)), body
        ))
      }).getOrElse({
        functionCall(CreateElement, Seq(
          dereference(StaticString(tag.name)), dereference(Attributes(tag)), body
        ))
      })
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
      val dereferencer = DereferencerImpl(scope.addToScope(Variable(identifier, ScopeElement.Default)), `package`)

      val inner = {
        val x = dereferencer.functionCall(ZipAndFlatten, Seq(
          StaticArray.expr(apply(body).map(toObservable))
        ))

        val arguments = Seq(Variable(identifier, ScopeElement.Default))

        anonymous(dereferenceScopeExpr(Seq(x)), arguments)
      }

      val outer = {
        val source = dereference(Reference(TemplateDereferencer.ElementsId))

        val mapFunctionDefinition = FunctionDefinition(Seq(ScopeElement.Default), ScopeElement.DefaultConst)
        val function = ExpressionOperation(Reference, Seq(source, ExpressionConstant(TemplateDereferencer.MapFunction.name, ScopeElement.DefaultConst)), ScopeElement.const(mapFunctionDefinition))

        val x = functionCall(ZipAndFlatten, Seq(
          functionCall(function, mapFunctionDefinition, Seq(inner))
        ))
        val arguments = Seq(Variable(TemplateDereferencer.ElementsId, ScopeElement.DefaultConst))

        anonymous(dereferenceScopeExpr(Seq(x)), arguments)
      }

      val mapFunctionDefinition = FunctionDefinition(Seq(ScopeElement.Default), ScopeElement.Default)

      val source = {
        val iterable = toObservable(functionCall(Reference(
          Tokenizer.apply(expression), Ast.identifier("toArray")
        ), Seq()))

        ExpressionOperation(Reference, Seq(iterable, ExpressionConstant(TemplateDereferencer.RxFlatMapFunction.name, ScopeElement.DefaultConst)), ScopeElement.const(mapFunctionDefinition))
      }

      functionCall(source, mapFunctionDefinition, Seq(outer))
  }

  private def anonymous(x: ExpressionOperation, arguments: Seq[Variable]): ExpressionOperation = {
    val func = FunctionDef2(FunctionReference.Anonymous, arguments)
    val funcDefinition = FunctionDefinition(arguments.map(_.`type`), x.`type`)

    ExpressionOperation(func, Seq(x), ScopeElement.const(funcDefinition))
  }
}
