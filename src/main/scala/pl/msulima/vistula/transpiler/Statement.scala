package pl.msulima.vistula.transpiler

import pl.msulima.vistula.scanner.ScanResult

object Statement {

  def apply(program: Seq[ScanResult]): Seq[String] = {
    program.map(apply)
  }

  def apply: PartialFunction[ScanResult, String] = {
    Expression.apply
  }
}
