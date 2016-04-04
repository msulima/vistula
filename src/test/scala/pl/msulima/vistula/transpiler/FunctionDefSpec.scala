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

    program.toScanned.map(Transpiler.apply).head must_==
      """function a(X) {
        |  var Y = X.map(function (X) {
        |    return X + 2;
        |  });
        |  return Y.map(function (Y) {
        |    return Y - 1;
        |  });
        |}""".stripMargin
  }
}
