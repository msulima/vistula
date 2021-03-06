package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.Vistula
import pl.msulima.vistula.testutil._

class TranspilerSpec extends Specification {

  def test(file: String) = {
    file in {
      Vistula.toJavaScript(readFile(s"/pl/msulima/vistula/transpiler/$file.vst")) must_== readFile(s"/pl/msulima/vistula/transpiler/$file.js")
    }
  }

  test("inline_javascript")
  test("lambda")
  test("primitives")
  test("templates")
}
