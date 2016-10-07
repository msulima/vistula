package pl.msulima.vistula.template.transpiler

import fastparse.all._
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.FunctionDef
import pl.msulima.vistula.transpiler.expression.data.{StaticArray, StaticString}
import pl.msulima.vistula.transpiler.expression.reference.{Declare, FunctionCall, Reference}
import pl.msulima.vistula.transpiler.scope.{ClassReference, Scope, ScopeElement, Variable}

case class Scoped(variables: Seq[Ast.identifier], body: Token)

object Template {

  val MagicInlineHtmlPrefix = "# html\n"
  val MagicClasspathHtmlRegex = "^# html:(.+?)".r

  private val ZipAndFlatten = Reference(Reference(Scope.VistulaHelper), Ast.identifier("zipAndFlatten"))
  private val Wrap = Reference(Reference(Scope.VistulaHelper), Ast.identifier("wrap"))
  private val IfChangedArrays = Reference(Reference(Scope.VistulaHelper), Ast.identifier("ifChangedArrays"))
  private val Dom = Reference(Reference(Scope.VistulaHelper), Ast.identifier("dom"))
  private val CreateBoundElement = Reference(Dom, Ast.identifier("createBoundElement"))
  private val CreateElement = Reference(Dom, Ast.identifier("createElement"))
  private val TextNode = Reference(Dom, Ast.identifier("textNode"))
  private val TextObservable = Reference(Dom, Ast.identifier("textObservable"))

  def apply(program: String): Token = {
    val nodes = apply((parser.Parser.document ~ End).parse(program).get.value)
    if (nodes.size == 1) {
      nodes.head
    } else {
      FunctionCall(ZipAndFlatten, Seq(StaticArray(nodes.map(Box))))
    }
  }

  private def apply(program: Seq[parser.Node]) = {
    program.map(applyScope)
  }

  private def applyScope(node: parser.Node) = {
    val Scoped(variables, body) = applyWithScope(node)

    if (variables.isEmpty) {
      body
    } else {
      val variableDeclarations = variables.map(variable => {
        Declare.introduce(variable, body = Operation(Reference, Seq(Constant("new vistula.ObservableImpl()"))),
          typedef = ClassReference.Object, mutable = true) // FIXME
      })

      val code = FunctionDef.anonymous(variableDeclarations :+ body)

      FunctionCall(Wrap, Seq(code))
    }
  }

  private def applyWithScope: PartialFunction[parser.Node, Scoped] = {
    case parser.Element(tag, childNodes) =>
      val children = childNodes.map(applyWithScope)
      val variables = children.flatMap(_.variables)
      val body = StaticArray(children.map(scoped => Box(scoped.body)))

      tag.id.map(id => {
        val code = FunctionCall(CreateBoundElement, Seq(
          StaticString(tag.name), Constant(id.name), Attributes(tag), body
        ))
        Scoped(variables :+ id, code)
      }).getOrElse({
        val code = FunctionCall(CreateElement, Seq(
          StaticString(tag.name), Attributes(tag), body
        ))

        Scoped(variables, code)
      })
    case other: parser.Node =>
      Scoped(Seq(), apply(other))
  }

  private def apply: PartialFunction[parser.Node, Token] = {
    case parser.ObservableNode(identifier) =>
      FunctionCall(TextObservable, Seq(
        Tokenizer.apply(identifier)
      ))
    case parser.IfNode(expr, body, elseBody) =>
      FunctionCall(IfChangedArrays, Seq(
        Tokenizer.apply(expr),
        StaticArray(apply(body).map(Box.apply)),
        StaticArray(apply(elseBody).map(Box.apply))
      ))
    case parser.TextNode(text) =>
      FunctionCall(TextNode, Seq(
        StaticString(text)
      ))
    case parser.LoopNode(identifier, expression, body) =>
      val iterable = FunctionCall(Reference(
        Tokenizer.apply(expression), Ast.identifier("toArray")
      ), Seq())

      val inner = FunctionDef.anonymous(Variable(identifier, ScopeElement.Default), Seq(
        FunctionCall(ZipAndFlatten, Seq(
          StaticArray(apply(body).map(Box.apply))
        ))
      ))

      val elementsId = Ast.identifier("$arg")

      val outer = FunctionDef.anonymous(Variable(elementsId, ScopeElement.DefaultConst), Seq(
        FunctionCall(ZipAndFlatten, Seq(
          FunctionCall(Reference(Reference(elementsId), Ast.identifier("map")), Seq(inner))
        ))
      ))

      Observable(FunctionCall(Reference(Box(iterable), Ast.identifier("rxFlatMap")), Seq(outer)))
  }
}
