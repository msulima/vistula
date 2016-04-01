package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast

object Statement {

  def apply: PartialFunction[Ast.stmt, Set[String]] = {
    If.apply
  }

  def applySeq(program: Seq[Ast.stmt]): Seq[FlatVariable] = {
    program.flatMap(apply2)
  }

  def apply2: PartialFunction[Ast.stmt, Seq[FlatVariable]] = {
    (Expression.apply2).orElse(FunctionDef.apply2).andThen(Flatter.apply)
  }
}
