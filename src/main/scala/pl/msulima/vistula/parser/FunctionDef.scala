package pl.msulima.vistula.parser

import fastparse.noApi._
import pl.msulima.vistula.parser.Expressions._
import pl.msulima.vistula.parser.WsApi._

object FunctionDef {

  val arglist = {
    val inits = P((plain_argument ~ !"=").rep(0, ","))
    val later = P(named_argument.rep(0, ",") ~ ",".? ~ ("*" ~ test).? ~ ",".? ~ ("**" ~ test).?)
    P(inits ~ ",".? ~ later)
  }

  private val plain_argument = P(test ~ comp_for.rep).map {
    case (x, Nil) => x
    case (x, gens) => Ast.expr.GeneratorExp(x, gens)
  }
  private val named_argument = P(NAME ~ "=" ~ test).map(Ast.keyword.tupled)

  val comp_for: P[Ast.comprehension] = P("for" ~ exprlist ~ "in" ~ or_test ~ comp_if.rep).map {
    case (targets, test, ifs) => Ast.comprehension(tuplize(targets), test, ifs)
  }
  private val comp_if: P[Ast.expr] = P("if" ~ test)

  val testlist1: P[Seq[Ast.expr]] = P(test.rep(1, sep = ","))

}
