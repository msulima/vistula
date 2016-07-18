package pl.msulima.vistula.template.transpiler

import fastparse.all._
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler.rpn.expression.data.StaticString
import pl.msulima.vistula.transpiler.rpn.expression.reference.FunctionCall
import pl.msulima.vistula.transpiler.rpn.{Constant, Operation, Tokenizer}
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
      Scoped(Seq(), apply(other))
  }

  private def apply: PartialFunction[parser.Node, String] = {
    case parser.ObservableNode(identifier) =>
      VistulaTranspiler(Operation(FunctionCall, Seq(Tokenizer.boxed(identifier)), Constant("vistula.dom.textObservable")))
    case parser.IfNode(expr, body, elseBody) =>
      s"vistula.ifChangedArrays(${VistulaTranspiler(expr)}, ${ToArray(apply(body))}, ${ToArray(apply(elseBody))})";
    case parser.TextNode(text) =>
      s"""vistula.dom.textNode(${escape(text)})""";
    case parser.ForNode(identifier, expression, body) =>
      val source = VistulaTranspiler(expression)

      val map =
        s"""return vistula.zipAndFlatten($$arg.map(function (${identifier.name}) {
            |${Indent.leftPad("return vistula.zipAndFlatten(" + ToArray(apply(body)) + ");")}
            |}))""".stripMargin

      s"""$source.rxFlatMap(function ($$arg) {
         |${Indent.leftPad(map)}
         |})""".stripMargin;
  }

  private def escape(text: String) =s""""${text.replaceAll("\n", """\\\n""")}""""

}
