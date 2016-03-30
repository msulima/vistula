package pl.msulima.vistula.transpiler

import pl.msulima.vistula.Ast.stmt
import pl.msulima.vistula.Nesting

object Statement {

  def apply: PartialFunction[stmt, String] = {
    apply(Nesting(Seq(0)))
  }

  def apply(nesting: Nesting): PartialFunction[stmt, String] = {
    If.apply.orElse(Expression.apply).orElse(FunctionDef.apply)
  }
}
