package pl.msulima.vistula.transpiler

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class ExpressionSpec extends Specification {

  "transpiles identity statement" in {
    val program =
      """
        |X
      """.stripMargin

    program.toJavaScript must_==
      "X;"
  }

  "transpiles simple statement" in {
    val program =
      """
        |2 + 2
      """.stripMargin

    program.toJavaScript must_==
      "vistula.constantObservable(2 + 2);"
  }

  "transpiles assignment" in {
    val program =
      """
        |let X = Y + 3
      """.stripMargin

    program.toJavaScript must_==
      """const X = Y.rxMap($arg => ($arg + 3));""".stripMargin
  }

  "transpiles function call" in {
    val program =
      """
        |let X = a(Y, 3)
      """.stripMargin

    program.toJavaScript must_==
      """const X = a(Y, vistula.constantObservable(3));""".stripMargin
  }

  "transpiles complex assignment" in {
    val program =
      """
        |let X = Y + 3 - a(Z + 1, 3)
      """.stripMargin

    program.toJavaScript must_==
      """const X = vistula.zip([
        |    Y.rxMap($arg => ($arg + 3)),
        |    a(Z.rxMap($arg => ($arg + 1)), vistula.constantObservable(3))
        |]).rxMap($args => ($args[0] - $args[1]));""".stripMargin
  }
}
