package pl.msulima.vistula

import fastparse.all._
import pl.msulima.vistula.parser.Statements


package object testutil {

  implicit class ToProgram(code: String) {

    def toStatement = {
      toProgram.head
    }

    def toProgram = {
      (Statements.file_input ~ End).parse(code).get.value
    }

    def toScanned = {
      pl.msulima.vistula.scanner.Statement.applySeq((Statements.file_input ~ End).parse(code).get.value)
    }
  }

}
