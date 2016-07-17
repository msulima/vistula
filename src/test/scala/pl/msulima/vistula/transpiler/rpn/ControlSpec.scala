package pl.msulima.vistula.transpiler.rpn

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil._

class ControlSpec extends Specification with TranspilerSpecification {

  def test = transpileAndCompare("control") _

  test("generator")
  test("function_def")
  test("if")
}
