package pl.msulima.vistula.statments

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class FunctionDefSpec extends Specification {

  "transpiles function definition" in {
    val program =
      """
        |def max3min0(y):
        |  y + 1
      """.stripMargin

    FunctionDef.apply(program.toStatement) must_==
      """
        |function x(y) {
        |  Rx.Observable.zip(y, function(y) {
        |    return y+1;
        |  }
        |};""".stripMargin
  }
}
