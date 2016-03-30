package pl.msulima.vistula.statments

import pl.msulima.vistula.Ast.stmt
import pl.msulima.vistula.Nesting

object Statement {

  def apply: PartialFunction[stmt, String] = {
    apply(Nesting(Seq(0)))
  }

  def apply(nesting: Nesting): PartialFunction[stmt, String] = {
    If(nesting).orElse(Expression(nesting))
  }
}
