package pl.msulima.vistula.html

import fastparse.all._
import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.readFile


class TranspilerSpec extends Specification {

  def parse(code: String) = {
    (Parser.document ~ End).parse(code).get.value
  }

  "transpile" in {
    Transpiler(TestData.SampleHtml) must_== readFile("/pl/msulima/vistula/html/sample.js")
  }
}
