package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class IfSpec extends Specification {

  "transpiles if" in {
    val program =
      """
        |if X < 3:
        |  X
        |else:
        |  3
      """.stripMargin

    program.toTranspiled.head must_==
      """X.map(function ($arg) {
        |    return $arg < 3;
        |}).flatMap(function ($ifCondition) {
        |    if ($ifCondition) {
        |        return X;
        |    } else {
        |        return vistula.constantObservable(3);
        |    }
        |})""".stripMargin
  }
}
