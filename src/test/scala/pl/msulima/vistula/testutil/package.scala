package pl.msulima.vistula

import fastparse.all._
import pl.msulima.vistula.parser.Statements

import scala.io.Source


package object testutil {

  def readFile(file: String) = {
    val inputStream = getClass.getResourceAsStream(file)
    require(inputStream != null, s"$file not found")

    val s = Source.fromInputStream(inputStream)
    val content = s.getLines().mkString("\n")
    s.close()
    content
  }

  implicit class ToProgram(code: String) {

    def toStatement = {
      toProgram.head
    }

    def toProgram = {
      (Statements.file_input ~ End).parse(code).get.value
    }
  }

}
