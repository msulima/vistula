package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class ExpressionSpec extends Specification {

  "transpiles assignment" in {
    val program =
      """
        |X = Y + 3
      """.stripMargin

    Statement.apply(program.toStatement) must_==
      """X + 3""".stripMargin
  }

  "transpiles function call" in {
    val program =
      """
        |a(X, Y)
      """.stripMargin

    Statement.apply(program.toStatement) must_==
      """Zip([X, Y]).flatMap(function (__args) {
        |  return a(__args[0], __args[1]);
        |});""".stripMargin
  }
}
