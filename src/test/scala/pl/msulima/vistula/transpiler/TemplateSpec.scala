package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.template.transpiler.Template
import pl.msulima.vistula.testutil.readFile


class TemplateSpec extends Specification {

  private def test(file: String) = {
    file in {
      Template(readFile(s"/pl/msulima/vistula/template/$file.vst.html")) must_== readFile(s"/pl/msulima/vistula/template/$file.js")
    }
  }

  test("sample")
  test("if")
  test("loop")
  test("form")
  test("event")
  test("variable")
}
