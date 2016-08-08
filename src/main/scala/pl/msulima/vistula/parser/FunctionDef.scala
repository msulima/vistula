package pl.msulima.vistula.parser

import fastparse.noApi._
import pl.msulima.vistula.parser.Expressions._
import pl.msulima.vistula.parser.Lexical.kw
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

  private val fpdef: P[Ast.expr] = P(NAME.map(Ast.expr.Name(_, Ast.expr_context.Param)))

  private val varargslist: P[Ast.arguments] = {
    val named_arg = P(fpdef ~ ("=" ~ test).?)
    val x = P(named_arg.rep(sep = ",") ~ ",".? ~ ("*" ~ NAME).?).map {
      case (normal_args, starargs) =>
        val (args, defaults) = normal_args.unzip
        Ast.arguments(args, starargs, defaults.flatten)
    }
    P(x)
  }
  private val parameters: P[Ast.arguments] = P("(" ~ varargslist ~ ")")

  val lambdef: P[Ast.expr.Lambda] = P(kw("lambda") ~ varargslist ~ ":" ~ test).map(Ast.expr.Lambda.tupled)
  val funcdef: P[Seq[Ast.expr] => Ast.stmt.FunctionDef] = P(kw("def") ~/ NAME ~ parameters ~ ":" ~~ Statements.suite).map {
    case (name, args, suite) => Ast.stmt.FunctionDef(name, args, suite, _)
  }
}