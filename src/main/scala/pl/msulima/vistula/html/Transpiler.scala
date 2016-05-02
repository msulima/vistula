package pl.msulima.vistula.html

import fastparse.all._
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Transpiler => VistulaTranspiler}
import pl.msulima.vistula.util.{Indent, ToArray}

object Transpiler {

  def apply(program: String): String = {
    apply((Statements.document ~ End).parse(program).get.value).mkString("", ";\n", ";")
  }

  def apply(program: Seq[Node]): Seq[String] = {
    program.map(apply)
  }

  def apply: PartialFunction[Node, String] = {
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
