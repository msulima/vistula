package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Assign {

  def apply: PartialFunction[Ast.stmt, String] = {
    case Ast.stmt.AssignStmt(stmt, value) =>
      s"${Transpiler(value)}.rxForEachOnce($$arg => ${Transpiler(stmt)}.rxPush($$arg))"
  }
}
