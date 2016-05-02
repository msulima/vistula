package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class IfSpec extends Specification {

  "transpiles if" in {
    val program =
      """
        |if X < 3:
        |  Y = X + 3
        |  Y
        |else:
        |  3
      """.stripMargin

    program.toJavaScript must_==
      """vistula.ifStatement(X.map(function ($arg) {
        |    return $arg < 3;
        |}), vistula.wrap(function () {
        |    var Y = X.map(function ($arg) {
        |        return $arg + 3;
        |    });
        |    return Y;
        |}), vistula.constantObservable(3))""".stripMargin
  }
}
