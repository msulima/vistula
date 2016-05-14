package pl.msulima.vistula.transpiler

import fastparse.all._
import pl.msulima.vistula.html
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
    val nodes = apply((html.Statements.document ~ End).parse(program).get.value)
    if (nodes.size == 1) {
      nodes.head
    } else {
      s"vistula.zipAndFlatten(${ToArray(nodes)})"
    }
  }

  private def apply(program: Seq[html.Node]): Seq[String] = {
    program.map(apply)
  }

  private def apply: PartialFunction[html.Node, String] = {
    case html.Element(tag, childNodes) =>
      val body = ToArray(childNodes.map(apply))
      s"""vistula.dom.createElement("${tag.name}", ${attributes(tag)}, $body)""".stripMargin;
    case html.ObservableNode(identifier) =>
      s"vistula.dom.textObservable(${VistulaTranspiler(Ast.stmt.Expr(identifier))})";
    case html.IfNode(expr, body, elseBody) =>
      s"vistula.dom.ifStatement(${VistulaTranspiler(Ast.stmt.Expr(expr))}, ${ToArray(apply(body))}, ${ToArray(apply(elseBody))})";
    case html.TextNode(text) =>
      s"""vistula.dom.textNode(${escape(text)})""";
    case html.ForNode(identifier, expression, body) =>
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

  private def attributes(tag: html.Tag) = {
    ToArray(tag.attributes.map({
      case html.AttributeValue(key, value) =>
        s"""["$key", ${VistulaTranspiler(Ast.stmt.Expr(value))}]"""
      case html.AttributeMarker(key) =>
        s"""["$key", ${VistulaTranspiler(Ast.stmt.Expr(Ast.expr.Name(Ast.identifier("None"), Ast.expr_context.Load)))}]"""
      case html.AttributeEvent(key, value) =>
        val arguments = Ast.arguments(Seq(Ast.expr.Name(Ast.identifier("ev"), Ast.expr_context.Param)), None, None, Seq())
        val function = Ast.stmt.FunctionDef(Ast.identifier(""), arguments, Seq(Ast.stmt.Expr(value)), Seq())

        s"""["($key)", ${VistulaTranspiler(function)}]"""
    }))
  }
}
