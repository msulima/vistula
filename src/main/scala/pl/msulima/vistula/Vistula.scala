package pl.msulima.vistula

import fastparse.all._
import pl.msulima.vistula.parser.{Ast, Statements}
import pl.msulima.vistula.scanner.ScanResult
import pl.msulima.vistula.transpiler.Statement

object Vistula {

  def toJavaScript(input: String): String = {
    transpile(scan(parse(input)))
  }

  private def parse(input: String) = {
    (Statements.file_input ~ End).parse(input).get.value
  }

  private def scan(program: Seq[Ast.stmt]): Seq[ScanResult] = {
    scanner.Scanner.apply(program)
  }

  private def transpile(program: Seq[ScanResult]) = {
    program.map(Statement.apply).mkString("\n")
  }
}
