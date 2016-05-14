package pl.msulima.vistula.html

import fastparse.all._
import pl.msulima.vistula.parser.Ast

object Lexical {

  private case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
    def apply(t: T) = f(t)

    override def toString() = name

  }

  private val StringChars = NamedFunction(!"<{".contains(_: Char), "StringChars")
  val strChars = P(CharsWhile(StringChars))

  val multilineSpace = P(CharsWhile(" \n\r".contains(_: Char)).?)
  val space = P(CharIn(" ").?)

  private val stringLiteral = pl.msulima.vistula.parser.Lexical.shortstring

  val expression = pl.msulima.vistula.parser.Expressions.expr

  val identifier = P(
    (pl.msulima.vistula.parser.Lexical.letter | "_") ~
      (pl.msulima.vistula.parser.Lexical.letter | pl.msulima.vistula.parser.Lexical.digit | "_").rep
  ).!.map(Ast.identifier)

  private val tagName = identifier.map(_.name)

  private val attributeExpression =
    P("\"" ~ inline(expression) ~ "\"")

  private val attributeValue =
    P(attributeExpression | stringLiteral.map(Ast.expr.Str))

  private val attribute =
    P(tagName.! ~ ("=" ~ attributeValue).?).map(Attribute.tupled)

  private val tagBody =
    P(tagName.! ~ space ~ attribute.rep(min = 0, sep = " ") ~ space).map(Tag.tupled)

  val selfClosingTag =
    P("<" ~ tagBody ~ "/>")

  val openTag =
    P("<" ~ tagBody ~ !"/" ~ ">")

  val closeTag =
    P("</" ~ tagName ~ ">").map(_ => ())

  def kw(s: String) = pl.msulima.vistula.parser.Lexical.kw(s)

  def inline[T](p: => Parser[T]): Parser[T] = {
    P("{{" ~ space ~ p ~ space ~ "}}")
  }

  def block[T](p: => Parser[T]): Parser[T] = {
    P("{%" ~ space ~ p ~ space ~ "%}")
  }
}
