package pl.msulima.vistula.transpiler.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{CodeTemplate, Static}

object Dereference {

  def apply: PartialFunction[Ast.expr, CodeTemplate] = {
    case Ast.expr.Dereference(value) =>
      CodeTemplate(s"%s.rxLastValue()", mapper = Static, Seq(value))
  }
}
