package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class ExpressionSpec extends Specification {

  "transpiles assignment" in {
    val program =
      """
        |X = Y + 3
      """.stripMargin

    program.toScanned.map(Statement.apply) must_== Seq(
      """var X = Y.map(function (Y) {
        |  return Y + 3;
        |});""".stripMargin
    )
  }

  "transpiles function call" in {
    val program =
      """
        |X = a(Y, 3)
      """.stripMargin

    program.toScanned.map(Statement.apply) must_== Seq(
      """var X = a(Y, 3);""".stripMargin
    )
  }
}
