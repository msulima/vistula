package pl.msulima.vistula.transpiler.rpn

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil._

class ReferenceSpec extends Specification with TranspilerSpecification {

  def test = transpileAndCompare("reference") _

  test("attribute")
  test("dereference")
  test("expression")
  test("function_call")
  test("method")
  test("mutable")
}