package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil._

class TranspilerSpec extends Specification {

  def test(file: String) = {
    file in {
      readFile(s"/pl/msulima/vistula/transpiler/$file.vst").toJavaScript must_== readFile(s"/pl/msulima/vistula/transpiler/$file.js")
    }
  }

  test("attribute")
  test("if")
  test("loop")
  test("method")
  test("primitives")
  test("templates")
}
