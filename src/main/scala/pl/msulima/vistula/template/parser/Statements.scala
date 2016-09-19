package pl.msulima.vistula.template.parser

import fastparse.noApi._
import pl.msulima.vistula.parser.Lexical.{kw => _}
import pl.msulima.vistula.parser.{Expressions => VistulaExpressions}
import pl.msulima.vistula.template.parser.Lexical._
import pl.msulima.vistula.template.parser.WsApi._

object Statements {

  val ifStatement: Parser[IfNode] = {
    val elseBlock = Expressions.block(Lexical.kw("else"))

    val endIf = Expressions.block(Lexical.kw("endif"))

    val elseStatement: P[Seq[Node]] = (elseBlock ~ Parser.document).?.map(_.getOrElse(Seq.empty))

    lazy val elif: P[Seq[Node]] = P(condStart("elif") ~ Parser.document ~ (elif | elseStatement)).map(node => Seq(IfNode.tupled(node)))

    P(condStart("if") ~ Parser.document ~/ (elif | elseStatement) ~ endIf).map(IfNode.tupled)
  }

  private def condStart(kw: String) = Expressions.block(Lexical.kw(kw) ~/ VistulaExpressions.comparison)

  val forStatement: Parser[LoopNode] = {
    val identifier = P(Lexical.kw("for") ~/ Lexical.identifier)
    val expression = P(Lexical.kw("in") ~/ Lexical.expression)

    val startFor = Expressions.block(identifier ~ expression)

    val endFor = Expressions.block(Lexical.kw("endfor"))

    P(startFor ~ Parser.document ~ endFor).map(LoopNode.tupled)
  }

  val variable =
    Expressions.inline(Lexical.expression).map(ObservableNode)

  val textNode: P[TextNode] =
    P(strChars.rep(min = 1).!).map(TextNode)

  val selfClosingElement: P[Element] =
    P(TagParser.selfClosingTag).map(x => Element(x, Seq()))
}
