package pl.msulima.vistula.scanner

import org.specs2.mutable.Specification
import pl.msulima.vistula.parser.Ast.expr.{BinOp, Name, Num}
import pl.msulima.vistula.parser.Ast.expr_context.Load
import pl.msulima.vistula.parser.Ast.identifier
import pl.msulima.vistula.parser.Ast.operator.Add
import pl.msulima.vistula.testutil.ToProgram

class ExpressionSpec extends Specification {

  "transpiles binary operation" in {
    val program =
      """
        |Y = 42
        |X = Y + 3
      """.stripMargin

    program.toScanned must_== Seq(
      FlatVariable("Y", Num(42), Seq()),
      FlatVariable("X", BinOp(Name(identifier("Y"), Load), Add, Num(3)), Seq(
        NamedObservable("Y")
      ))
    )
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

    program.toScanned must_== result.toScanned
  }
}
