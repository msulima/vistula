package pl.msulima.vistula.html

import fastparse.all._
import pl.msulima.vistula.util.Indent

object Transpiler {

  def apply(program: String): String = {
    apply((Parser.document ~ End).parse(program).get.value).mkString("", ";\n", ";")
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
    case TextNode(text) =>
      s"""document.createTextNode(${escape(text)})""";
  }

  private def escape(text: String) =s""""${text.replaceAll("\n", """\\\n""")}""""
}
