package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._

object Pass {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.Pass =>
      Constant("")
  }
}
