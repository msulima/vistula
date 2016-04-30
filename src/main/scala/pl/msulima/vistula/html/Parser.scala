package pl.msulima.vistula.html

import fastparse.all._
import pl.msulima.vistula.parser.Ast

object Parser {

  private case class NamedFunction[T, V](f: T => V, name: String) extends (T => V) {
    def apply(t: T) = f(t)

    override def toString() = name

  }

  private val Whitespace = NamedFunction(" \r\n".contains(_: Char), "Whitespace")
  private val StringChars = NamedFunction(!"<>{}".contains(_: Char), "StringChars")

  private val space = P(CharsWhile(Whitespace).?)

  private val hexDigit = P(CharIn('0' to '9', 'a' to 'f', 'A' to 'F'))
  private val unicodeEscape = P("u" ~ hexDigit ~ hexDigit ~ hexDigit ~ hexDigit)
  private val escape = P("\\" ~ (CharIn("\"/\\bfnrt") | unicodeEscape))

  private val strChars = P(CharsWhile(StringChars))

  private val letter = {
    val lowercase = P(CharIn('a' to 'z'))
    val uppercase = P(CharIn('A' to 'Z'))
    P(lowercase | uppercase)
  }

  private val openTag =
    P("<" ~ letter.rep.! ~ ">")

  private val closeTag =
    P("</" ~ letter.rep ~ ">")

  private val variable =
    P("{{" ~ space ~ letter.rep.! ~ space ~ "}}").map(name => ObservableNode(Ast.identifier(name)))

  private val textNode: P[TextNode] =
    P(strChars.rep(min = 1).!).map(TextNode)

  private val element: P[Element] =
    P(space ~ openTag ~/ node.rep(min = 0) ~ closeTag).map(Element.tupled)

  private val node: P[Node] =
    P(space ~ (textNode | element | variable) ~ space)

  val document: P[Seq[Node]] =
    P(node.rep)
}
