package pl.msulima.vistula.transpiler.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._

object ClassDef {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case classDef@Ast.stmt.ClassDef(identifier, Nil, body, Nil) =>
      IntroduceClass(classDef)
  }
}
