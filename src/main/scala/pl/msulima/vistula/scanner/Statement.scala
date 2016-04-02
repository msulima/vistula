package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast

object Statement {

  def applySeq(program: Seq[Ast.stmt]): Seq[FlatVariable] = {
    program.flatMap(apply)
  }

  def apply: PartialFunction[Ast.stmt, Seq[FlatVariable]] = {
    Expression.apply.orElse(FunctionDef.apply2).andThen(Flatter.apply)
  }
}
