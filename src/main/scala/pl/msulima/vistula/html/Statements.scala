package pl.msulima.vistula.html

import fastparse.all._
import pl.msulima.vistula.html.Lexical._
import pl.msulima.vistula.parser.Expressions

object Statements {

  val variable =
    Lexical.inline(Lexical.identifier).map(ObservableNode)

  val ifStatement: Parser[IfNode] = {
    val ifStart = Lexical.block(Lexical.kw("if") ~ Lexical.space ~ Expressions.comparison)

    val elseBlock = Lexical.block(Lexical.kw("else"))

    val endIf = Lexical.block(Lexical.kw("endif"))

    P(ifStart ~ document ~ elseBlock ~ document ~ endIf).map(IfNode.tupled)
  }

  val textNode: P[TextNode] =
    P(strChars.rep(min = 1).!).map(TextNode)

  val element: P[Element] =
    P(multilineSpace ~ openTag ~/ node.rep(min = 0) ~ closeTag).map(Element.tupled)

  val node: P[Node] =
    P(multilineSpace ~ (textNode | element | ifStatement | variable) ~ multilineSpace)

  val document: P[Seq[Node]] =
    P(node.rep)
}
