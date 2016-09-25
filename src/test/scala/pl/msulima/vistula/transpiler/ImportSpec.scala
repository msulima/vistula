package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil._
import pl.msulima.vistula.{Package, Vistula}

class ImportSpec extends Specification with TranspilerSpecification {

  def test = transpileAndCompare("import") _

  test("import")

  "merge multiple files from package into one" in {
    Vistula.browserify(Package("examples.import"))

    readFile("target/vistula/classes/examples.import.js") must_==
      readFile("/pl/msulima/vistula/transpiler/import/examples.import.js")
  }
}
