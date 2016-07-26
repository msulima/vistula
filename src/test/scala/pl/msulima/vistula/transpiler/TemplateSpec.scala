package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.TranspilerSpecification


class TemplateSpec extends Specification with TranspilerSpecification {

  def test = transpileAndCompareHtml("template") _

  test("sample")
  test("if")
  test("loop")
  test("form")
  test("event")
  test("variable")
}
