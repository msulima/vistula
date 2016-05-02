package pl.msulima.vistula.html

import fastparse.all._
import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.readFile


class TranspilerSpec extends Specification {

  def parse(code: String) = {
    (Statements.document ~ End).parse(code).get.value
  }

  "transpile" in {
    Transpiler(readFile("/pl/msulima/vistula/html/sample.html")) must_== readFile("/pl/msulima/vistula/html/sample.js")
  }

  "transpile if" in {
    Transpiler(readFile("/pl/msulima/vistula/html/if.html")) must_== readFile("/pl/msulima/vistula/html/if.js")
  }
}
