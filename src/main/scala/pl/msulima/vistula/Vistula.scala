package pl.msulima.vistula

import fastparse.all._
import pl.msulima.vistula.parser.{Ast, Statements}

object Vistula {

  def toJavaScript(input: String): String = {
    transpile(scan(parse(input)))
  }

  private def parse(input: String) = {
    (Statements.file_input ~ End).parse(input).get.value
  }

  private def scan(program: Seq[Ast.stmt]): Seq[Observable] = {
    program.map(statement => {
      Observable(scanner.Statement.apply(statement), statement)
    })
  }

  private def transpile(program: Seq[Observable]) = {
    program.map(obs => {
      transpiler.Statement.apply(obs.statement)
    }).mkString("\n\n")
  }
}

case class Observable(variables: Set[String], statement: Ast.stmt)
