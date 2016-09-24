package pl.msulima.vistula.transpiler.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._

object Other {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case stmt@Ast.stmt.ClassDef(identifier, Nil, body, Nil) =>
      Direct(stmt)
    case stmt@Ast.stmt.Import(Ast.alias(identifier, None) +: _) =>
      Direct(stmt)
  }
}
