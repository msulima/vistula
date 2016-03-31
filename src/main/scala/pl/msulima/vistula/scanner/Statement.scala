package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast.stmt

object Statement {

  def apply: PartialFunction[stmt, Set[String]] = {
    If.apply.orElse(Expression.apply).orElse(FunctionDef.apply)
  }

  def apply2: PartialFunction[stmt, Variable] = {
    Expression.apply2
  }
}
