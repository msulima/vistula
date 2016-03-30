package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class ExpressionSpec extends Specification {

  "transpiles function definition" in {
    val program =
      """
        |x + y(z)
      """.stripMargin

    Statement.apply(program.toStatement) must_==
      """x + y(z)""".stripMargin
  }
}
