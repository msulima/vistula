package pl.msulima.vistula.template.parser

import fastparse.all._
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser.Lexical.multilineSpace

object TagParser {

  private val tagName = Lexical.identifier.map(_.name)

  private val tagId = P("#" ~ Lexical.identifier)

  private val attributeExpression =
    P("\"" ~ Expressions.inline(Lexical.expression) ~ "\"")

  private val attributeValue =
    P(attributeExpression | Lexical.stringLiteral.map(Ast.expr.Str))

  private val attribute =
    P(tagName.! ~ "=" ~ attributeValue).map(AttributeValue.tupled)

  private val attributeMarker =
    P(tagName).map(AttributeMarker)

  private val event =
    P("(" ~ tagName.! ~ ")=" ~ attributeExpression).map(AttributeEvent.tupled)

  private val tagAttributes = (attribute | attributeMarker | event).rep(min = 0, sep = multilineSpace)

  private val tagBody =
    P(tagName.! ~ multilineSpace ~ tagId.? ~ multilineSpace ~ tagAttributes ~ multilineSpace).map(Tag.tupled)

  val selfClosingTag: Parser[Tag] =
    P("<" ~ tagBody ~ "/>")

  val openTag: Parser[Tag] =
    P("<" ~ tagBody ~ ">")

  val closeTag: Parser[Unit] =
    P("</" ~ tagName ~ ">").map(_ => ())
}
