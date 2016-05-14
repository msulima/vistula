package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class FunctionDefSpec extends Specification {

  "transpiles function definition" in {
    val program =
      """
        |def a(X):
        |  Y = X + 2
        |  Y - 1
      """.stripMargin

    program.toJavaScript must_==
      """function a(X) {
        |    const Y = X.rxMap($arg => ($arg + 2));
        |    return Y.rxMap($arg => ($arg - 1));
        |};""".stripMargin
  }
}
