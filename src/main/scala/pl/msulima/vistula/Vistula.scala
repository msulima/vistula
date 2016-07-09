package pl.msulima.vistula

import fastparse.all._
import pl.msulima.vistula.parser.Statements
import pl.msulima.vistula.transpiler.{Transpiler, rpn}

object Vistula {

  def toJavaScript(input: String): String = {
    Transpiler(parse(input))
  }

  def toJavaScriptRpn(input: String): String = {
    rpn.Transpiler.scoped(parse(input))
  }

  private def parse(input: String) = {
    (Statements.file_input ~ End).parse(input).get.value
  }
}
