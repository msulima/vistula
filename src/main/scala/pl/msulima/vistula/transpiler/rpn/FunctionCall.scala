package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast

case object FunctionCall extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Call(Ast.expr.Name(Ast.identifier(func), Ast.expr_context.Load), args, _, _, _) =>
      Observable(Operation(FunctionCall, Constant(func) +: args.map(expr => Box(Tokenizer.apply(expr)))))
    case Ast.expr.Call(func, args, _, _, _) =>
      val call = Operation(FunctionCall, Constant("call") +: args.map(expr => Box(Tokenizer.apply(expr))))

      Operation(RxFlatMap, Box(Tokenizer.apply(func)) :: call :: Nil)
  }

  override def apply(operands: List[Constant]): Constant = {
    Constant(s"${operands.head.value}(${operands.tail.map(_.value).mkString(", ")})")
  }
}
