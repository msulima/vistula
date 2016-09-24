package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil._

class ImportSpec extends Specification with TranspilerSpecification {

  def test = transpileAndCompare("import") _

  test("import")
}
