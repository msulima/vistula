package pl.msulima.vistula.scanner

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class ExpressionSpec extends Specification {

  "transpiles function call" in {
    val program =
      """
        |x + y(z)
      """.stripMargin

    Statement.apply(program.toStatement) must_== Set("x", "y", "z")
  }
}
