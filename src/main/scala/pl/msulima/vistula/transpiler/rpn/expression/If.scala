package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn._
import pl.msulima.vistula.util.Indent

object If extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.If(testExpr, body, orElse) =>
      val b = Box(Operation(Wrap, body.map(Tokenizer.applyStmt), Constant("ignore")))
      val e = Box(Operation(Wrap, orElse.map(Tokenizer.applyStmt), Constant("ignore")))

      Operation(If, Seq(b, e), Tokenizer.apply(testExpr))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    val transpiled =
      s"""${output.value},
         |${operands.head.value},
         |${operands(1).value}""".stripMargin

    Constant(
      s"""vistula.ifStatement(
          |${Indent.leftPad(transpiled)}
          |)""".stripMargin)
  }
}
