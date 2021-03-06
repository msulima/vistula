package pl.msulima.vistula.parser

import fastparse.noApi._
import pl.msulima.vistula.parser.Lexical.kw
import pl.msulima.vistula.parser.Statements._
import pl.msulima.vistula.parser.WsApi._

object IfParser {

  val if_stmt: P[Ast.stmt.If] = {
    val bracketsTest = P("(" ~ Expressions.test ~ ")")

    val firstIf = P(kw("if") ~/ bracketsTest ~ Statements.suite)
    val elifs = P((space_indents ~~ kw("elif") ~/ bracketsTest ~ Statements.suite).repX)
    val lastElse = P((space_indents ~~ kw("else") ~/ Statements.suite).?)
    P(firstIf ~~ elifs ~~ lastElse).map {
      case (test, body, elifs, orelse) =>
        val (init :+ last) = (test, body) +: elifs
        val (last_test, last_body) = last
        init.foldRight(Ast.stmt.If(last_test, last_body, orelse.toSeq.flatten)) {
          case ((test, body), rhs) => Ast.stmt.If(test, body, Seq(rhs))
        }
    }
  }
}
