package pl.msulima.vistula.template.parser

import pl.msulima.vistula.parser.Ast

object WsApi extends fastparse.WhitespaceApi.Wrapper({
  import fastparse.all._
  NoTrace(" ".rep)
})

object Lexical {

  import fastparse.all._

  private case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
    def apply(t: T) = f(t)

    override def toString() = name
  }

  private val StringChars = NamedFunction(!"<{".contains(_: Char), "StringChars")
  val strChars = P(CharsWhile(StringChars))

  val multilineSpace = P(CharsWhile(" \n\r".contains(_: Char)).?)

  val stringLiteral = pl.msulima.vistula.parser.Lexical.shortstring

  val expression = pl.msulima.vistula.parser.Expressions.expr

  val identifier = P(
    (pl.msulima.vistula.parser.Lexical.letter | "_") ~
      (pl.msulima.vistula.parser.Lexical.letter | pl.msulima.vistula.parser.Lexical.digit | "_" | "-").rep
  ).!.map(Ast.identifier)

  def kw(s: String) = pl.msulima.vistula.parser.Lexical.kw(s)

}
