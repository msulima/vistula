package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil._
import pl.msulima.vistula.{Package, Vistula}

class ModulesSpec extends Specification with TranspilerSpecification {

  def test = transpileAndCompare("modules") _

  test("import")
  test("predef")

  "merge multiple files from package into one" in {
    Vistula.browserify(Package("examples.modules"))

    readFile("target/vistula/classes/examples.modules.js") must_==
      readFile("/pl/msulima/vistula/transpiler/modules/examples.modules.js")
  }
}
