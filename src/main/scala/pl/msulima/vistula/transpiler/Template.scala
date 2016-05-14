package pl.msulima.vistula.transpiler

import fastparse.all._
import pl.msulima.vistula.html._
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Transpiler => VistulaTranspiler}
import pl.msulima.vistula.util.{Indent, ToArray}

import scala.io.Source

object Template {

  private val MagicInlineHtmlPrefix = "# html\n"
  private val MagicClasspathHtmlRegex = "^# html:(.+?)".r

  val parseExpression: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Str(MagicClasspathHtmlRegex(sourceFile)) =>
      val stream = getClass.getResourceAsStream(sourceFile)
      require(stream != null, s"$sourceFile not found")
      val source = Source.fromInputStream(stream)
      val lines = source.getLines().toList
      source.close()

      Fragment(Template(lines.mkString("\n")))
    case Ast.expr.Str(x) if x.startsWith(MagicInlineHtmlPrefix) =>
      Fragment(Template(x.stripPrefix(MagicInlineHtmlPrefix)))
  }

  def apply(program: String): String = {
    val nodes = apply((Statements.document ~ End).parse(program).get.value)
    if (nodes.size == 1) {
      nodes.head
    } else {
      s"vistula.zipAndFlatten(${ToArray(nodes)})"
    }
  }

  private def apply(program: Seq[Node]): Seq[String] = {
    program.map(apply)
  }

  private def apply: PartialFunction[Node, String] = {
    case Element(tag, childNodes) =>
      val body = ToArray(childNodes.map(apply))
      s"""vistula.dom.createElement("${tag.name}", ${attributes(tag)}, $body)""".stripMargin;
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

  private def attributes(tag: Tag) = {
    ToArray.toCompact(tag.attributes.map({
      case (key, value) =>
        s""""$key"""" -> VistulaTranspiler(Ast.stmt.Expr(value))
    }))
  }
}
