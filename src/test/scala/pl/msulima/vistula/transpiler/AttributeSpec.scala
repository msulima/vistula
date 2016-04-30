package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class AttributeSpec extends Specification {

  "transpiles attribute access" in {
    val program =
      """
        |W.X + Y.Z
      """.stripMargin

    program.toTranspiled.head must_==
      """vistula.zip([W.flatMap(function ($arg) {
        |    return $arg.X;
        |}), Y.flatMap(function ($arg) {
        |    return $arg.Z;
        |})]).map(function ($args) {
        |    return $args[0] + $args[1];
        |})""".stripMargin
  }
}
