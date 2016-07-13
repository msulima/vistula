package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn._
import pl.msulima.vistula.util.Indent

object Loop extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.For(Ast.expr.Name(Ast.identifier(name), Ast.expr_context.Load), iterExpr, body, _) =>
      val iter = Tokenizer.boxed(iterExpr)

      Operation(Loop, Seq(Constant(name), iter), Observable(Transformer.returnLast(body)))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    val map =
      s"""vistula.zip($$arg.map(${operands.head.value} => {
          |${Indent.leftPad(output.value)}
          |}))""".stripMargin

    Constant( s"""${operands(1).value}.rxFlatMap($$arg => ($map))""".stripMargin)
  }
}
