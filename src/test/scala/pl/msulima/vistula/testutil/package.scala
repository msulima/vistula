package pl.msulima.vistula

import fastparse.all._
import pl.msulima.vistula.parser.Statements
import pl.msulima.vistula.transpiler.Transpiler


package object testutil {

  implicit class ToProgram(code: String) {

    def toStatement = {
      toProgram.head
    }

    def toProgram = {
      (Statements.file_input ~ End).parse(code).get.value
    }

    def toTranspiled = {
      (Statements.file_input ~ End).parse(code).get.value.map(Transpiler.apply)
    }
  }

}
