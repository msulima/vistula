package pl.msulima.vistula

import fastparse.all._
import pl.msulima.vistula.parser.Statements
import pl.msulima.vistula.scanner.ResultVariable


package object testutil {

  implicit class ToProgram(code: String) {

    def toStatement = {
      toProgram.head
    }

    def toProgram = {
      (Statements.file_input ~ End).parse(code).get.value
    }

    def toScannedVariables = {
      toScanned.flatMap({
        case ResultVariable(variables) =>
          variables
      })
    }

    def toScanned = {
      pl.msulima.vistula.scanner.Scanner.apply((Statements.file_input ~ End).parse(code).get.value)
    }
  }

}
