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
        |__W_1 = X + 3
        |__W_5 = b(Y)
        |__W_4 = a(__W_5, Z)
        |W = __W_1 + __W_4
      """.stripMargin

    Flatter(Statement.apply2(program.toStatement)) must_== Statement.applySeq(result.toProgram)
  }
}
