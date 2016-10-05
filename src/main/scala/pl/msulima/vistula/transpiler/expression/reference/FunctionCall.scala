package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._

object FunctionCall {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Call(func, args, _, _, _) =>
      FunctionCall(Tokenizer.apply(func), args.map(Tokenizer.apply))
  }

  def apply(func: Token, args: Seq[Token]): Operation = {
    Operation(FunctionCall(constructor = false), func +: args)
  }
}

case class FunctionCall(constructor: Boolean) extends Operator {

  override def apply(operands: List[Constant]) = {
    val prefix = if (constructor) {
      "new "
    } else {
      ""
    }
    s"$prefix${operands.head.value}(${operands.tail.map(_.value).mkString(", ")})"
  }
}
