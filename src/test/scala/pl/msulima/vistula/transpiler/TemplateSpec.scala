package pl.msulima.vistula.transpiler

import fastparse.all._
import org.specs2.mutable.Specification
import pl.msulima.vistula.html.Statements
import pl.msulima.vistula.testutil.readFile


class TemplateSpec extends Specification {

  def parse(code: String) = {
    (Statements.document ~ End).parse(code).get.value
  }

  "transpile" in {
    Template(readFile("/pl/msulima/vistula/html/sample.html")) must_== readFile("/pl/msulima/vistula/html/sample.js")
  }

  "transpile if" in {
    Template(readFile("/pl/msulima/vistula/html/if.html")) must_== readFile("/pl/msulima/vistula/html/if.js")
  }
}
