package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil._

class TypesystemSpec extends Specification with TranspilerSpecification {

  def test = transpileAndCompare("typesystem") _

  test("class")
  test("default_constructor")
  test("function_arguments")
  test("function_output")
  test("method")
  test("variable")
}
