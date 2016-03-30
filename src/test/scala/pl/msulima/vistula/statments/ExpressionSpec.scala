package pl.msulima.vistula.statments

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class ExpressionSpec extends Specification {

  "transpiles function definition" in {
    val program =
      """
        |x + y
      """.stripMargin

    Statement.apply(program.toStatement) must_==
      """
        |Rx.Observable.zip(x, y, function(x, y) {
        |  return x+y;
        |});""".stripMargin
  }
}
