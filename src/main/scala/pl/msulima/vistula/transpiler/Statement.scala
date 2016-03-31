package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Statement {

  lazy val apply: PartialFunction[Ast.stmt, String] = {
    If.apply.orElse(Expression.apply).orElse(FunctionDef.apply)
  }
}
