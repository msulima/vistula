package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class JustExpressionSpec extends Specification {

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
      """var X = Zip([Y]).map(function ($args) {
        |  return $args[0] + 3;
        |});""".stripMargin
    )
  }

  "transpiles function call" in {
    val program =
      """
        |X = a(Y, 3)
      """.stripMargin

    program.toTranspiled must_== Seq(
      """var X = a(Y, ConstantObservable(3));""".stripMargin
    )
  }

  "transpiles complex assignment" in {
    val program =
      """
        |X = Y + 3 - a(Z + 1, 3)
      """.stripMargin

    program.toTranspiled.head must_==
      """var X = Zip([Zip([Y]).map(function ($args) {
        |  return $args[0] + 3;
        |}),a(Zip([Z]).map(function ($args) {
        |  return $args[0] + 1;
        |}), ConstantObservable(3))]).map(function ($args) {
        |  return $args[0] - $args[1];
        |});""".stripMargin
  }
}
