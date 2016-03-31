package pl.msulima.vistula.scanner

import org.specs2.mutable.Specification
import pl.msulima.vistula.testutil.ToProgram

class ExpressionSpec extends Specification {

  "transpiles binary operation" in {
    val program =
      """
        |T = X + 3
      """.stripMargin

    val result =
      """
        |T1 = X + 3
      """.stripMargin

    Statement.apply2(program.toStatement) must_== Statement.apply2(result.toStatement)
  }

  "transpiles function call" in {
    val program =
      """
        |W = X + 3 + a(b(Y), Z)
      """.stripMargin

    val result =
      """
        |__W_2 = b(Y)
        |__W_3 = a(__W_2, Z)
        |__W_1 = X + 3
        |W = __W_1 + __W_3
      """.stripMargin

    Statement.apply2(program.toStatement) must_== Statement.apply2(result.toStatement)
  }
}
