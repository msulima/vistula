package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil._

class TypeSystemSpec extends Specification with TranspilerSpecification {

  def test = transpileAndCompare("typesystem") _

  test("class")
  test("function_arguments")
  test("function_output")
  test("variable")
}
