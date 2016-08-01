package pl.msulima.vistula.template.transpiler

import fastparse.all._
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.control.FunctionDef
import pl.msulima.vistula.transpiler.expression.data.{StaticArray, StaticString}
import pl.msulima.vistula.transpiler.expression.reference.{Declare, FunctionCall, Reference}
import pl.msulima.vistula.transpiler.scope.{Identifier, Variable}

case class Scoped(variables: Seq[Ast.identifier], body: Token)

object Template {

  def apply(program: String): Token = {
    val nodes = apply((parser.Statements.document ~ End).parse(program).get.value)
    if (nodes.size == 1) {
      nodes.head
    } else {
      FunctionCall("vistula.zipAndFlatten", Seq(StaticArray(nodes.map(Box))))
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
        Introduce(
          Variable(variable, Identifier(observable = true)),
          Operation(Declare, Seq(Constant(variable.name)), Constant("new vistula.ObservableImpl()")) // FIXME
        )
      })

      val code = FunctionDef.anonymous(variableDeclarations :+ body)

      FunctionCall("vistula.wrap", Seq(code))
    }
  }

  private def applyWithScope: PartialFunction[parser.Node, Scoped] = {
    case parser.Element(tag, childNodes) =>
      val children = childNodes.map(applyWithScope)
      val variables = children.flatMap(_.variables)
      val body = StaticArray(children.map(scoped => Box(scoped.body)))

      tag.id.map(id => {
        val code = FunctionCall(Constant("vistula.dom.createBoundElement"), Seq(
          StaticString(tag.name), Constant(id.name), Attributes(tag), body
        ))
        Scoped(variables :+ id, code)
      }).getOrElse({
        val code = FunctionCall(Constant("vistula.dom.createElement"), Seq(
          StaticString(tag.name), Attributes(tag), body
        ))

        Scoped(variables, code)
      })
    case other: parser.Node =>
      Scoped(Seq(), apply(other))
  }

  private def apply: PartialFunction[parser.Node, Token] = {
    case parser.ObservableNode(identifier) =>
      FunctionCall(Constant("vistula.dom.textObservable"), Seq(
        Tokenizer.apply(identifier)
      ))
    case parser.IfNode(expr, body, elseBody) =>
      FunctionCall("vistula.ifChangedArrays", Seq(
        Tokenizer.apply(expr),
        StaticArray(apply(body).map(Box.apply)),
        StaticArray(apply(elseBody).map(Box.apply))
      ))
    case parser.TextNode(text) =>
      FunctionCall(Constant("vistula.dom.textNode"), Seq(
        StaticString(text)
      ))
    case parser.LoopNode(identifier, expression, body) =>
      val iterable = Box(FunctionCall(Reference(
        Tokenizer.apply(expression), Constant("toArray")
      ), Seq()))

      val inner = FunctionDef.anonymous(identifier, Seq(
        FunctionCall("vistula.zipAndFlatten", Seq(
          StaticArray(apply(body).map(Box.apply))
        ))
      ))

      val elementsId = Ast.identifier("$arg")

      val outer = FunctionDef.anonymous(elementsId, Seq(
        FunctionCall("vistula.zipAndFlatten", Seq(
          FunctionCall(Reference(Reference(elementsId), Constant("map")), Seq(inner))
        ))
      ), mutableArgs = false)

      Observable(FunctionCall(Reference(iterable, Constant("rxFlatMap")), Seq(outer)))
  }
}
