package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.scanner.FlatVariable

object Statement {

  def apply2: PartialFunction[FlatVariable, String] = {
    Expression.apply2
  }

  lazy val apply: PartialFunction[Ast.stmt, String] = {
    If.apply.orElse(Expression.apply).orElse(FunctionDef.apply)
  }
}
