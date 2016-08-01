package pl.msulima.vistula.transpiler.expression.control

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Tokenizer, _}


case object Return extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.Return(Some(value)) =>
      Return(Tokenizer.apply(value))
  }

  def apply(body: Token): Token = {
    Operation(Return, Seq(body))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"return ${operands.head.value}")
  }
}