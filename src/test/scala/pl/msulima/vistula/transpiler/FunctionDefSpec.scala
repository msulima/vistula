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

    program.toTranspiled.head must_==
      """function a(X) {
        |  var Y = Zip([X]).map(function ($args) {
        |    return $args[0] + 2;
        |  });
        |  return Zip([Y]).map(function ($args) {
        |    return $args[0] - 1;
        |  });
        |}""".stripMargin
  }
}
