package pl.msulima.vistula.transpiler

import fastparse.all._
import pl.msulima.vistula.html.{TextNode, _}
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
      s"vistula.dom.textObservable(${identifier.name})";
    case IfNode(expr, body, elseBody) =>
      s"vistula.dom.ifStatement(${VistulaTranspiler(Ast.stmt.Expr(expr))}, ${ToArray(apply(body))}, ${ToArray(apply(elseBody))})";
    case TextNode(text) =>
      s"""vistula.dom.textNode(${escape(text)})""";
  }

  private def escape(text: String) =s""""${text.replaceAll("\n", """\\\n""")}""""
}
