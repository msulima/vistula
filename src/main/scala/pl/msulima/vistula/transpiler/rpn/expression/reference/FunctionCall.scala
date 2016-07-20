package pl.msulima.vistula.transpiler.rpn.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Tokenizer, _}

case object FunctionCall extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Call(func, args, _, _, _) =>
      FunctionCall(Tokenizer.apply(func), args.map(Tokenizer.apply))
  }

  def apply(func: Token, args: Seq[Token]): Token = {
    Operation(FunctionCall, args, func)
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"${operands.head.value}(${operands.tail.map(_.value).mkString(", ")})")
  }
}
