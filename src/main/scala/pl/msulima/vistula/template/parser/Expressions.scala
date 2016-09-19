package pl.msulima.vistula.template.parser

import fastparse.noApi._
import pl.msulima.vistula.template.parser.WsApi._

object Expressions {

  def inline[T](p: => Parser[T]): Parser[T] = {
    P("{{" ~ p ~ "}}")
  }

  def block[T](p: => Parser[T]): Parser[T] = {
    P("{%" ~ p ~ "%}")
  }
}
