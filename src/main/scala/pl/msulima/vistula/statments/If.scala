package pl.msulima.vistula.statments

import pl.msulima.vistula.Ast.cmpop.{Gt, GtE, Lt, LtE}
import pl.msulima.vistula.Ast.expr.{Compare, Name, Num}
import pl.msulima.vistula.Ast.expr_context.Load
import pl.msulima.vistula.Ast.{expr, identifier, stmt}
import pl.msulima.vistula.Nesting

object If {

  // filter?

  def apply(nesting: Nesting): PartialFunction[stmt, String] = {
    case stmt.If(test, body, orElse) =>
      s"""var ${nesting.extend} = null;
          |if (${parseTest(test)}) {
          |${Statement(nesting.extend)(body.head)};
          |} ${orElse.map(parseOrElse)})
          |var $nesting = ${nesting.extend};""".stripMargin
  }

  lazy val parseOrElse: PartialFunction[stmt, String] = {
    case stmt.If(test, body, orelse) =>
      s"""
         |else
         |};""".stripMargin
    case expression: stmt.Expr =>
      Statement(expression)
  }

  lazy val parseTest: PartialFunction[expr, String] = {
    case Compare(Name(identifier(id), Load), op +: _, Num(value) +: _) =>
      val opString = op match {
        case Lt => "<"
        case LtE => "<="
        case Gt => ">"
        case GtE => ">="
      }
      s"$id $opString $value"
  }
}
