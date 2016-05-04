package pl.msulima.vistula.html

import fastparse.all._

object Lexical {

  private case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
    def apply(t: T) = f(t)

    override def toString() = name

  }

  private val StringChars = NamedFunction(!"<{".contains(_: Char), "StringChars")
  val strChars = P(CharsWhile(StringChars))

  val multilineSpace = P(CharsWhile(" \n\r".contains(_: Char)).?)
  val space = P(CharIn(" ").?)

  private val letter = {
    val lowercase = P(CharIn('a' to 'z'))
    val uppercase = P(CharIn('A' to 'Z'))
    P(lowercase | uppercase)
  }

  val stringLiteral = pl.msulima.vistula.parser.Lexical.shortstring

  val tagName =
    P(letter.rep(min = 0))

  val attribute =
    P(tagName.! ~ "=" ~ stringLiteral.!)

  private val tagBody =
    P(tagName.! ~ space ~ attribute.rep(min = 0, sep = " ") ~ space).map(Tag.tupled)

  val selfClosingTag =
    P("<" ~ tagBody ~ "/>")

  val openTag =
    P("<" ~ tagBody ~ ">")

  val closeTag =
    P("</" ~ tagName ~ ">")

  def kw(s: String) = pl.msulima.vistula.parser.Lexical.kw(s)

  val expression = pl.msulima.vistula.parser.Expressions.expr

  val identifier = pl.msulima.vistula.parser.Lexical.identifier

  def inline[T](p: => Parser[T]): Parser[T] = {
    P("{{" ~ space ~ p ~ space ~ "}}")
  }

  def block[T](p: => Parser[T]): Parser[T] = {
    P("{%" ~ space ~ p ~ space ~ "%}")
  }
}
