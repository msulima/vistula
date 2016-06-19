package pl.msulima.vistula

import fastparse.all._
import pl.msulima.vistula.parser.Statements
import pl.msulima.vistula.transpiler.Transpiler

object Vistula {

  def toJavaScript(input: String): String = {
    Transpiler.scoped(parse(input))
  }

  private def parse(input: String) = {
    (Statements.file_input ~ End).parse(input).get.value
  }
}
