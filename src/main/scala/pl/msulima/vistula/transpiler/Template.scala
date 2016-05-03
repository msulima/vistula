package pl.msulima.vistula.transpiler

import fastparse.all._
import pl.msulima.vistula.html._
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Transpiler => VistulaTranspiler}
import pl.msulima.vistula.util.{Indent, ToArray}

object Template {

  private val MagicInlineHtmlPrefix = "# html\n"

  val parseExpression: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Str(x) if x.startsWith(MagicInlineHtmlPrefix) =>
      Fragment(Template(x.stripPrefix(MagicInlineHtmlPrefix)))
  }

  def apply(program: String): String = {
    s"vistula.zipAndFlatten(${ToArray(apply((Statements.document ~ End).parse(program).get.value))})"
  }

  private def apply(program: Seq[Node]): Seq[String] = {
    program.map(apply)
  }

  private def apply: PartialFunction[Node, String] = {
    case Element(tagName, childNodes) =>
      s"""vistula.dom.createElement(document.createElement("$tagName"), [
          |${Indent.leftPad(childNodes.map(apply).mkString(",\n"))}
          |])""".stripMargin;
    case ObservableNode(identifier) =>
      s"vistula.dom.textObservable(${VistulaTranspiler(Ast.stmt.Expr(identifier))})";
    case IfNode(expr, body, elseBody) =>
      s"vistula.dom.ifStatement(${VistulaTranspiler(Ast.stmt.Expr(expr))}, ${ToArray(apply(body))}, ${ToArray(apply(elseBody))})";
    case TextNode(text) =>
      s"""vistula.dom.textNode(${escape(text)})""";
    case ForNode(identifier, expression, body) =>
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
