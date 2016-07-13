package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Tokenizer, _}

case object FunctionCall extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Call(Ast.expr.Name(Ast.identifier(func), Ast.expr_context.Load), args, _, _, _) =>
      Operation(FunctionCall, Constant(func) +: args.map(expr => Tokenizer.boxed(expr)), Observable(Constant("ignore")))
    case Ast.expr.Call(func, args, _, _, _) =>
      Operation(FunctionCall, Tokenizer.apply(func) +: args.map(expr => Tokenizer.boxed(expr)), Observable(Constant("ignore")))
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"${operands.head.value}(${operands.tail.map(_.value).mkString(", ")})")
  }
}
