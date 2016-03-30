package pl.msulima.vistula.scanner

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

    Statement.apply(program.toStatement) must_== Set("x", "y", "z")
  }
}
