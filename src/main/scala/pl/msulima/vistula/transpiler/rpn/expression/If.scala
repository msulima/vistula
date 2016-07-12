package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn._
import pl.msulima.vistula.util.Indent

case object If extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.If(testExpr, body, orElse) =>
      Operation(If, Seq(
        Tokenizer.boxed(testExpr),
        Transformer.wrapAndReturnLast(body),
        Transformer.wrapAndReturnLast(orElse)
      ), Observable(Constant("ignore")))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    val transpiled =
      s"""${operands.head.value},
         |${operands(1).value},
         |${operands(2).value}""".stripMargin

    Constant(
      s"""vistula.ifStatement(
          |${Indent.leftPad(transpiled)}
          |)""".stripMargin)
  }
}
