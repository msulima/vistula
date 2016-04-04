package pl.msulima.vistula.scanner

import org.specs2.mutable.Specification
import pl.msulima.vistula.parser.Ast.cmpop._
import pl.msulima.vistula.parser.Ast.expr._
import pl.msulima.vistula.parser.Ast.expr_context.Load
import pl.msulima.vistula.parser.Ast.identifier
import pl.msulima.vistula.parser.Ast.operator._
import pl.msulima.vistula.testutil.ToProgram

import scala.collection.mutable.ArrayBuffer

class IfSpec extends Specification {

  "transpiles if" in {
    val program =
      """
        |if X < 3:
        |  3
        |else:
        |  Z = Y + 3
        |  Z % 2
      """.stripMargin

    If.apply(program.toStatement) must_== ResultIf(
      ResultVariable(Seq(
        FlatVariable(None, Compare(Name(identifier("X"), Load), ArrayBuffer(Lt), Seq(Num(3))), Seq(NamedObservable(identifier("X")))))
      ),
      Seq(
        ResultVariable(Seq(FlatVariable(None, Num(3), Seq())))
      ),
      Seq(
        ResultVariable(Seq(
          FlatVariable(Some(identifier("Z")), BinOp(Name(identifier("Y"), Load), Add, Num(3)), Seq(NamedObservable(identifier("Y")))))
        ),
        ResultVariable(Seq(
          FlatVariable(None, BinOp(Name(identifier("Z"), Load), Mod, Num(2)), Seq(NamedObservable(identifier("Z")))))
        )
      )
    )
  }
}
