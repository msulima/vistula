package pl.msulima.vistula.template.transpiler

import fastparse.all._
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
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
        Scoped(variables :+ id, s"""vistula.dom.createBoundElement("${tag.name}", ${id.name}, ${Attributes(tag)}, $body)""")
      }).getOrElse({
        Scoped(variables, s"""vistula.dom.createElement("${tag.name}", ${Attributes(tag)}, $body)""")
      })
    case other: parser.Node =>
      Scoped(Seq(), apply(other))
  }

  private def apply: PartialFunction[parser.Node, String] = {
    case parser.ObservableNode(identifier) =>
      s"vistula.dom.textObservable(${VistulaTranspiler(Ast.stmt.Expr(identifier))})";
    case parser.IfNode(expr, body, elseBody) =>
      s"vistula.dom.ifStatement(${VistulaTranspiler(Ast.stmt.Expr(expr))}, ${ToArray(apply(body))}, ${ToArray(apply(elseBody))})";
    case parser.TextNode(text) =>
      s"""vistula.dom.textNode(${escape(text)})""";
    case parser.ForNode(identifier, expression, body) =>
      val source = VistulaTranspiler(Ast.stmt.Expr(expression))

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
