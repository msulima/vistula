package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Tokenizer, _}

object FunctionCall extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Call(func, args, _, _, _) =>
      FunctionCall(Tokenizer.apply(func), args.map(Tokenizer.apply))
  }

  def apply(func: String, args: Seq[Token]): Operation = {
    Operation(FunctionCall, Reference(func) +: args)
  }

  def apply(func: Token, args: Seq[Token]): Operation = {
    Operation(FunctionCall, func +: args)
  }

  override def apply(operands: List[Constant]) = {
    s"${operands.head.value}(${operands.tail.map(_.value).mkString(", ")})"
  }
}
