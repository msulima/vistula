package pl.msulima.vistula.scanner

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class FunctionDefSpec extends Specification {

  "transpiles function definition" in {
    val program =
      """
        |def max3min0(y):
        |  return y + 1
      """.stripMargin

    Statement.apply(program.toStatement) must_== Set("y")
  }
}
