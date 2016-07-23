package pl.msulima.vistula.template.parser

import fastparse.noApi._
import pl.msulima.vistula.parser.Lexical.{kw => _}
import pl.msulima.vistula.parser.{Expressions => VistulaExpressions}
import pl.msulima.vistula.template.parser.Lexical._
import pl.msulima.vistula.template.parser.WsApi._

object Statements {

  val ifStatement: Parser[IfNode] = {
    val ifStart = Expressions.block(Lexical.kw("if") ~/ VistulaExpressions.comparison)

    val elseBlock = Expressions.block(Lexical.kw("else"))

    val endIf = Expressions.block(Lexical.kw("endif"))

    val elseStatement = (elseBlock ~ document).?.map(_.getOrElse(Seq.empty))

    P(ifStart ~ document ~ elseStatement ~ endIf).map(IfNode.tupled)
  }

  val forStatement: Parser[LoopNode] = {
    val identifier = P(Lexical.kw("for") ~/ Lexical.identifier)
    val expression = P(Lexical.kw("in") ~/ Lexical.expression)

    val startFor = Expressions.block(identifier ~ expression)

    val endFor = Expressions.block(Lexical.kw("endfor"))

    P(startFor ~ document ~ endFor).map(LoopNode.tupled)
  }

  val variable =
    Expressions.inline(Lexical.expression).map(ObservableNode)

  val textNode: P[TextNode] =
    P(strChars.rep(min = 1).!).map(TextNode)

  val selfClosingElement: P[Element] =
    P(TagParser.selfClosingTag).map(x => Element(x, Seq()))

  val element: P[Element] =
    P(TagParser.openTag ~ node.rep(min = 0) ~ TagParser.closeTag).map(Element.tupled)

  val node: P[Node] =
    P(element | selfClosingElement | forStatement | ifStatement | variable | textNode)

  lazy val document: P[Seq[Node]] =
    P(multilineSpace ~ node.rep ~ multilineSpace)
}
