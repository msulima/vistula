package pl.msulima.vistula

import fastparse.all._


package object testutil {

  implicit class ToProgram(code: String) {

    def toStatement = {
      toProgram.head
    }

    def toProgram = {
      (Statements.file_input ~ End).parse(code).get.value
    }
  }

}
