package pl.msulima.vistula.transpiler

import pl.msulima.vistula.scanner.ScanResult

object Transpiler {

  def apply(program: Seq[ScanResult]): Seq[String] = {
    program.map(apply)
  }

  def apply: PartialFunction[ScanResult, String] = {
    Expression.apply.orElse(FunctionDef.apply).orElse(If.apply)
  }
}
