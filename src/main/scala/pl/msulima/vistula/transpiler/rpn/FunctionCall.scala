package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast

object FunctionCall extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Call(Ast.expr.Name(Ast.identifier(func), Ast.expr_context.Load), args, _, _, _) =>
      ConstantOperation(FunctionCall, ConstantOperand(func) +: args.map(expr => Tokenizer.box(expr)))
    case Ast.expr.Call(func, args, _, _, _) =>
      ConstantOperation(RxFlatMap, args.map(expr => Tokenizer.box(expr)))
  }

  override def apply(operands: List[ConstantOperand]): ConstantOperand = {
    ConstantOperand(s"${operands.head.value}(${operands.tail.map(_.value).mkString(", ")})")
  }
}
