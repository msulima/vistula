package pl.msulima.vistula.template.parser

import fastparse.all._
import pl.msulima.vistula.template.parser.Statements._

object Parser {

  val element: P[Element] =
    P(TagParser.openTag ~ node.rep(min = 0) ~ TagParser.closeTag).map(Element.tupled)

  val node: P[Node] =
    P(element | selfClosingElement | forStatement | ifStatement | variable | textNode)

  lazy val document: P[Seq[Node]] =
    P(Lexical.multilineSpace ~ node.rep ~ Lexical.multilineSpace)
}
