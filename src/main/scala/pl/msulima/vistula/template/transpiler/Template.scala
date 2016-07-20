package pl.msulima.vistula.template.transpiler

import fastparse.all._
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler.rpn._
import pl.msulima.vistula.transpiler.rpn.expression.control.{FunctionDef, Return}
import pl.msulima.vistula.transpiler.rpn.expression.data.{StaticArray, StaticString}
import pl.msulima.vistula.transpiler.rpn.expression.reference.{FunctionCall, Reference}
import pl.msulima.vistula.transpiler.{Transpiler => VistulaTranspiler}
import pl.msulima.vistula.util.{Indent, ToArray}

case class Scoped(variables: Seq[Ast.identifier], body: String)

object Template {

  def apply(program: String): String = {
    val nodes = apply((parser.Statements.document ~ End).parse(program).get.value)
    if (nodes.size == 1) {
      nodes.head
    } else {
      s"vistula.zipAndFlatten(${ToArray(nodes)})"
    }
  }

  private def apply(program: Seq[parser.Node]): Seq[String] = {
    program.map(applyScope)
  }

  private def applyScope(node: parser.Node): String = {
    val Scoped(variables, body) = applyWithScope(node)

    if (variables.isEmpty) {
      body
    } else {
      val variableDeclarations = variables.map(variable => s"const ${variable.name} = new vistula.ObservableImpl();")

      s"""vistula.wrap(() => {
          |${Indent.leftPad(variableDeclarations)}
          |${Indent.leftPad("return " + body + ";")}
          |})""".stripMargin
    }
  }

  private def applyWithScope: PartialFunction[parser.Node, Scoped] = {
    case parser.Element(tag, childNodes) =>
      val children = childNodes.map(applyWithScope)
      val variables = children.flatMap(_.variables)
      val body = ToArray(children.map(_.body))

      tag.id.map(id => {
        val code = VistulaTranspiler(Operation(FunctionCall, Seq(
          StaticString(tag.name), Constant(id.name), Attributes(tag), Constant(body)
        ), Constant("vistula.dom.createBoundElement")))
        Scoped(variables :+ id, code)
      }).getOrElse({
        val code = VistulaTranspiler(Operation(FunctionCall, Seq(
          StaticString(tag.name), Attributes(tag), Constant(body)
        ), Constant("vistula.dom.createElement")))

        Scoped(variables, code)
      })
    case other: parser.Node =>
      Scoped(Seq(), VistulaTranspiler(apply(other)))
  }

  private def apply: PartialFunction[parser.Node, Token] = {
    case parser.ObservableNode(identifier) =>
      Operation(FunctionCall, Seq(
        Tokenizer.boxed(identifier)
      ), Constant("vistula.dom.textObservable"))
    case parser.IfNode(expr, body, elseBody) =>
      Operation(FunctionCall, Seq(
        Tokenizer.boxed(expr),
        StaticArray(apply(body).map(Constant.apply)),
        StaticArray(apply(elseBody).map(Constant.apply))
      ), Constant("vistula.ifChangedArrays"))
    case parser.TextNode(text) =>
      Operation(FunctionCall, Seq(
        StaticString(text)
      ), Constant("vistula.dom.textNode"))
    case parser.ForNode(identifier, expression, body) =>
      val x = Operation(FunctionDef, Seq(
        Constant(""),
        Constant(identifier.name)
      ), Operation(Return, Seq(
        Operation(FunctionCall, Seq(
          StaticArray(apply(body).map(Constant.apply))
        ), Constant("vistula.zipAndFlatten"))
      ), Tokenizer.Ignored))

      val call = Operation(FunctionCall, Seq(
        x
      ), Operation(Reference, Seq(
        Constant("$arg")
      ), Constant("map")))

      val map = Operation(FunctionDef, Seq(
        Constant(""),
        Constant("$arg")
      ), Operation(Return, Seq(
        Operation(FunctionCall, Seq(
          call
        ), Constant("vistula.zipAndFlatten"))
      ), Tokenizer.Ignored))

      Operation(FunctionCall, Seq(
        map
      ), Operation(Reference, Seq(
        Tokenizer.boxed(expression)
      ), Constant("rxFlatMap")))
  }
}
