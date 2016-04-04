package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast

object Scanner {

  def apply(program: Seq[Ast.stmt]): Seq[ScanResult] = {
    program.map(apply)
  }

  def apply: PartialFunction[Ast.stmt, ScanResult] = {
    Expression.apply.orElse(FunctionDef.apply).orElse(If.apply)
  }
}
