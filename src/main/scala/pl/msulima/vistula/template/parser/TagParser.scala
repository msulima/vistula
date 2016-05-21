package pl.msulima.vistula.template.parser

import fastparse.noApi._
import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template.parser.WsApi._

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

  private val tagBody =
    P(tagName.! ~ tagId.? ~ (attribute | attributeMarker | event).rep(min = 0)).map(Tag.tupled)

  val selfClosingTag: Parser[Tag] =
    P("<" ~ tagBody ~ "/>")

  val openTag: Parser[Tag] =
    P("<" ~ tagBody ~ !"/" ~ ">")

  val closeTag: Parser[Unit] =
    P("</" ~ tagName ~ ">").map(_ => ())
}
