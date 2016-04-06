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

    program.toScanned.map(Transpiler.apply).head must_==
      """var __ifCondition = X.map(function (X) {
        |  return X < 3;
        |});
        |return __ifCondition.flatMap(function (__ifCondition) {
        |  if (__ifCondition) {
        |    return X;
        |  } else {
        |    return Observable(3);
        |  }
        |});""".stripMargin
  }
}
