package pl.msulima.vistula.transpiler.rpn

import org.specs2.mutable.Specification
import pl.msulima.vistula.Vistula
import pl.msulima.vistula.testutil._

class TranspilerSpec extends Specification {

  def test(file: String) = {
    file in {
      Vistula.toJavaScriptRpn(readFile(s"/pl/msulima/vistula/transpiler/$file.vst")) must_== readFile(s"/pl/msulima/vistula/transpiler/$file.js")
    }
  }

  test("attribute")
  test("change")
  test("expression")
  test("function")
  test("if")
  test("inline_javascript")
  test("lambda")
  test("loop")
  test("method")
  test("primitives")
  test("templates")
  test("scope/dereference")
  test("scope/mutable")
}
