package pl.msulima.vistula.html

import fastparse.all._
import pl.msulima.vistula.html.Lexical._
import pl.msulima.vistula.parser.Expressions
import pl.msulima.vistula.parser.Lexical.{kw => _}

object Statements {

  val ifStatement: Parser[IfNode] = {
    val ifStart = Lexical.block(Lexical.kw("if") ~ Lexical.space ~/ Expressions.comparison)

    val elseBlock = Lexical.block(Lexical.kw("else"))

    val endIf = Lexical.block(Lexical.kw("endif"))

    P(ifStart ~ document ~ elseBlock ~ document ~ endIf).map(IfNode.tupled)
  }

  val forStatement: Parser[ForNode] = {
    val identifier = P(Lexical.kw("for") ~ Lexical.space ~/ Lexical.identifier)
    val expression = P(Lexical.kw("in") ~ Lexical.space ~/ Lexical.expression)

    val ifStart = Lexical.block(identifier ~ Lexical.space ~ expression)

    val endFor = Lexical.block(Lexical.kw("endfor"))

    P(ifStart ~ document ~ endFor).map(ForNode.tupled)
  }

  val variable =
    Lexical.inline(Lexical.expression).map(ObservableNode)

  val textNode: P[TextNode] =
    P(strChars.rep(min = 1).!).map(TextNode)

  val selfClosingElement: P[Element] =
    P(selfClosingTag).map(x => Element(x, Seq()))

  val element: P[Element] =
    P(openTag ~ node.rep(min = 0) ~ closeTag).map(Element.tupled)

  val node: P[Node] =
    P(element | selfClosingElement | forStatement | ifStatement | variable | textNode)

  val document: P[Seq[Node]] =
    P(multilineSpace ~ node.rep ~ multilineSpace)
}
