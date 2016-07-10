package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Tokenizer, _}


case object Return extends Operator {

  def apply: PartialFunction[Ast.stmt, Token] = {
    case Ast.stmt.Return(Some(value)) =>
      Operation(Return, Seq(Tokenizer.apply(value)), Constant("ignore"))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"return ${operands.head.value}")
  }
}
