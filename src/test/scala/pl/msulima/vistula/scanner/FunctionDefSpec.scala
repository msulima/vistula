package pl.msulima.vistula.scanner

import org.specs2.mutable.Specification
import pl.msulima.vistula.parser.Ast.expr._
import pl.msulima.vistula.parser.Ast.expr_context._
import pl.msulima.vistula.parser.Ast.identifier
import pl.msulima.vistula.parser.Ast.operator.Add
import pl.msulima.vistula.testutil.ToProgram

class FunctionDefSpec extends Specification {

  "transpiles function definition" in {
    val program =
      """
        |def a(X):
        |  Y = X + 1
        |  Y + 2
      """.stripMargin

    val y = FlatVariable(Some(identifier("Y")), BinOp(Name(identifier("X"), Load), Add, Num(1)), Seq(
      NamedObservable(identifier("X"))
    ))

    val result = FlatVariable(None, BinOp(Name(identifier("Y"), Load), Add, Num(2)), Seq(
      NamedObservable(identifier("Y"))
    ))

    val body = Seq(
      ResultVariable(Seq(
        y
      )),
      ResultVariable(Seq(
        result
      ))
    )

    program.toScanned must_== Seq(
      Function(identifier("a"), Seq(identifier("X")), body)
    )
  }
}
