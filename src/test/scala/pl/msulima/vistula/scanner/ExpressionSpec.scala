package pl.msulima.vistula.scanner

import org.specs2.mutable.Specification
import pl.msulima.vistula.parser.Ast.expr.{BinOp, Name, Num}
import pl.msulima.vistula.parser.Ast.expr_context.Load
import pl.msulima.vistula.parser.Ast.identifier
import pl.msulima.vistula.parser.Ast.operator.{Add, Sub}
import pl.msulima.vistula.testutil.ToProgram

class ExpressionSpec extends Specification {

  "transpiles binary operation" in {
    val program =
      """
        |X = 42
        |Y = X + 3
        |Y - 8
      """.stripMargin

    program.toScannedVariables must_== Seq(
      FlatVariable(Some(identifier("X")), Num(42), Seq()),
      FlatVariable(Some(identifier("Y")), BinOp(Name(identifier("X"), Load), Add, Num(3)), Seq(
        NamedObservable(identifier("X"))
      )),
      FlatVariable(None, BinOp(Name(identifier("Y"), Load), Sub, Num(8)), Seq(
        NamedObservable(identifier("Y"))
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

    program.toScannedVariables must_== result.toScannedVariables
  }
}
