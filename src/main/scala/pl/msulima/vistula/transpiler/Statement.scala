package pl.msulima.vistula.transpiler

import pl.msulima.vistula.scanner.FlatVariable

object Statement {

  def apply(program: Seq[FlatVariable]): Seq[String] = {
    program.map(apply)
  }

  def apply: PartialFunction[FlatVariable, String] = {
    Expression.apply
  }
}
