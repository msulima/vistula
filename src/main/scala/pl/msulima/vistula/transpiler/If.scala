package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.parser.Ast.stmt

object If {

  // filter?

  lazy val apply: PartialFunction[stmt, String] = {
    case stmt.If(test, body, orElse) =>
      s"""if (${Expression.parseExpression(test)}) {
          |  ${body.map(Statement.apply).mkString("\n")}
          |} ${orElse.map(parseOrElse).mkString("\n")}""".stripMargin
  }

  lazy val parseOrElse: PartialFunction[stmt, String] = {
    case x@stmt.If(test, body, orelse) =>
      s"else ${apply(x)}"
    case x: stmt.Expr =>
      s"""else {
          |  ${Expression.apply(x)}
          |}""".stripMargin
  }

}
