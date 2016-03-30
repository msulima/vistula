package pl.msulima.vistula.scanner

import pl.msulima.vistula.Ast.stmt

object Statement {

  def apply: PartialFunction[stmt, Set[String]] = {
    If.apply.orElse(Expression.apply).orElse(FunctionDef.apply)
  }
}
