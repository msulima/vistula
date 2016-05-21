package pl.msulima.vistula.template.transpiler

import fastparse.all._
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser
import pl.msulima.vistula.transpiler.{Transpiler => VistulaTranspiler}
import pl.msulima.vistula.util.{Indent, ToArray}

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
    program.map(apply)
  }

  private def apply: PartialFunction[parser.Node, String] = {
    case parser.Element(tag, childNodes) =>
      val body = ToArray(childNodes.map(apply))
      s"""vistula.dom.createElement("${tag.name}", ${Attributes(tag)}, $body)""".stripMargin;
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
