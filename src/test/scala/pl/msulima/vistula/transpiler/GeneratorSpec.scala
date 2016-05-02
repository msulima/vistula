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
      """var W = vistula.aggregate(X, Z, ($acc, $source) => {
        |    let Y = vistula.constantObservable($acc);
        |    let Z = vistula.constantObservable($source);
        |    return vistula.zip([
        |        Y,
        |        Z
        |    ]).map(function ($args) {
        |        return $args[0] + $args[1];
        |    });
        |});""".stripMargin
  }
}
