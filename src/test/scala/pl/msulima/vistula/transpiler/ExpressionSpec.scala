package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class ExpressionSpec extends Specification {

  "transpiles simple statement" in {
    val program =
      """
        |X
      """.stripMargin

    program.toTranspiled must_== Seq(
      "X"
    )
  }

  "transpiles assignment" in {
    val program =
      """
        |X = Y + 3
      """.stripMargin

    program.toTranspiled must_== Seq(
      """var X = Y.map(function ($arg) {
        |  return $arg + 3;
        |});""".stripMargin
    )
  }

  "transpiles function call" in {
    val program =
      """
        |X = a(Y, 3)
      """.stripMargin

    program.toTranspiled must_== Seq(
      """var X = a(Y, vistula.constantObservable(3));""".stripMargin
    )
  }

  "transpiles complex assignment" in {
    val program =
      """
        |X = Y + 3 - a(Z + 1, 3)
      """.stripMargin

    program.toTranspiled.head must_==
      """var X = vistula.zip([Y.map(function ($arg) {
        |  return $arg + 3;
        |}), a(Z.map(function ($arg) {
        |  return $arg + 1;
        |}), vistula.constantObservable(3))]).map(function ($args) {
        |  return $args[0] - $args[1];
        |});""".stripMargin
  }
}
