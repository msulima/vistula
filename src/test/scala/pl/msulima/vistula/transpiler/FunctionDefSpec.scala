package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class FunctionDefSpec extends Specification {

  "transpiles function definition" in {
    val program =
      """
        |def x(y):
        |  return y + 1
      """.stripMargin

    Statement.apply(program.toStatement) must_==
      """
        |function x(y) {
        |  return y + 1;
        |};""".stripMargin
  }
}
