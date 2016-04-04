package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class IfSpec extends Specification {

  "transpiles if" in {
    val program =
      """
        |if y < 3:
        |  x
        |elif y > 0:
        |  z
        |else:
        |  3
      """.stripMargin

    Transpiler.apply(program.toScanned) must_==
      """if (y < 3) {
        |  x
        |} else if (y > 0) {
        |  z
        |} else {
        |  3
        |}""".stripMargin
  }
}
