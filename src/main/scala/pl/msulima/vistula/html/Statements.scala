package pl.msulima.vistula.html

import fastparse.all._
import pl.msulima.vistula.html.Lexical._
import pl.msulima.vistula.parser.Expressions

object Statements {

  val ifStatement: Parser[IfNode] = {
    val ifStart = Lexical.block(Lexical.kw("if") ~ Lexical.space ~ Expressions.comparison)

    val elseBlock = Lexical.block(Lexical.kw("else"))

    val endIf = Lexical.block(Lexical.kw("endif"))

    P(ifStart ~ document ~ elseBlock ~ document ~ endIf).map(IfNode.tupled)
  }

  val variable =
    Lexical.inline(Lexical.expression).map(ObservableNode)

  val textNode: P[TextNode] =
    P(strChars.rep(min = 1).!).map(TextNode)

  val element: P[Element] =
    P(openTag ~/ node.rep(min = 0) ~ closeTag).map(Element.tupled)

  val node: P[Node] =
    P(element | ifStatement | variable | textNode)

  val document: P[Seq[Node]] =
    P(multilineSpace ~ node.rep ~ multilineSpace)
}
