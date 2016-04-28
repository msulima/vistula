package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class GeneratorSpec extends Specification {

  "transpiles generator" in {
    val program =
      """
        |W = (X or Y + Z for Y in Z)
      """.stripMargin

    program.toTranspiled.head must_==
      """var W = aggregate(X, Z, ($acc, $source) => {
        |  let Y = ConstantObservable($acc);
        |  let Z = ConstantObservable($source);
        |  return Zip([Y, Z]).map(function ($args) {
        |    return $args[0] + $args[1];
        |  });
        |});""".stripMargin
  }
}
