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
        |T4 = b(Y)
        |T5 = a(T2, Z)
        |T2 = X + 3
        |W = T2 + T5
      """.stripMargin

    Statement.apply2(program.toStatement) must_== Statement.apply2(result.toStatement)
  }
}
