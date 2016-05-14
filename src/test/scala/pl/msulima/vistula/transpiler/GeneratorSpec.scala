package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class GeneratorSpec extends Specification {

  "transpiles generator" in {
    val program =
      """
        |W = (X or Y + Z for Y in Z)
      """.stripMargin

    program.toJavaScript must_==
      """const W = vistula.aggregate(X, Z, ($acc, $source) => {
        |    const Y = vistula.constantObservable($acc);
        |    const Z = vistula.constantObservable($source);
        |    return vistula.zip([
        |        Y,
        |        Z
        |    ]).rxMap($args => ($args[0] + $args[1]));
        |});""".stripMargin
  }
}
