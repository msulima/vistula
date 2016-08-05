package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Tokenizer, _}


case object Return extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.Return(Some(value)) =>
      Operation(Return, Seq(Tokenizer.apply(value)))
  }

  def apply(body: Expression): Expression = {
    ExpressionOperation(Return, Seq(body), body.`type`)
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"return ${operands.head.value}")
  }
}
