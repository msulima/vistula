package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil._

class AttributeSpec extends Specification {

  "transpiles attribute access" in {
    readFile("/pl/msulima/vistula/transpiler/attribute.vst").toJavaScript must_== readFile("/pl/msulima/vistula/transpiler/attribute.js")
  }

  "transpiles method calls" in {
    readFile("/pl/msulima/vistula/transpiler/method.vst").toJavaScript must_== readFile("/pl/msulima/vistula/transpiler/method.js")
  }
}
