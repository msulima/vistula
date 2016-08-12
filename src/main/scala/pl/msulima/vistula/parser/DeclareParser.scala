package pl.msulima.vistula.parser

import fastparse.noApi._
import pl.msulima.vistula.parser.Expressions._
import pl.msulima.vistula.parser.Lexical.kw
import pl.msulima.vistula.parser.Statements._
import pl.msulima.vistula.parser.WsApi._

object DeclareParser {

  val declare_stmt: P[Ast.stmt] = P(declare_factory(kw("let"), mutable = true) | declare_factory(kw("const"), mutable = false))

  private def declare_factory(prefix: P[Unit], mutable: Boolean): P[Ast.stmt.DeclareStmt] = {
    P(prefix ~ Lexical.identifier ~ (":" ~ typedef).? ~ "=" ~ (small_stmt | compound_stmt)).map({
      case (target, maybeTypedef, value) => Ast.stmt.DeclareStmt(target, value, mutable, maybeTypedef.getOrElse(Seq()))
    })
  }

  val assign_stmt: P[Ast.stmt] = P(expr ~ "<-" ~ (small_stmt | compound_stmt)).map(Ast.stmt.AssignStmt.tupled)
}
