package pl.msulima.vistula.scanner

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.Indent

trait ScanResult

case class ResultFunction(name: Ast.identifier, arguments: Seq[Ast.identifier], body: Seq[ScanResult]) extends ScanResult

case class ResultIf(test: ResultVariable, body: Seq[ScanResult], other: Seq[ScanResult]) extends ScanResult

case class ResultVariable(variables: Seq[FlatVariable]) extends ScanResult

case class FlatVariable(name: Option[Ast.identifier], expression: Ast.expr, dependsOn: Seq[NamedObservable]) {

  override def toString: String = {
    s"""
       |${Indent.ind(0)} ${name.map(_.name)} = $expression${dependsOn.map(_.prettyPrint(1)).mkString("")}""".stripMargin
  }
}
