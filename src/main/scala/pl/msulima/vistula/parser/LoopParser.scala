package pl.msulima.vistula.parser


import fastparse.noApi._
import pl.msulima.vistula.parser.Lexical.kw
import pl.msulima.vistula.parser.WsApi._

object LoopParser {

  val for_stmt: P[Ast.stmt.For] = {
    P(kw("for") ~/ "(" ~ Expressions.exprlist ~ kw("in") ~ Expressions.testlist ~ ")" ~ Statements.suite).map {
      case (itervars, generator, body) =>
        Ast.stmt.For(Expressions.tuplize(itervars), Expressions.tuplize(generator), body)
    }
  }
}
