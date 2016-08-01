package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Tokenizer, _}

case object FunctionCall extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Call(func, args, _, _, _) =>
      FunctionCall(Tokenizer.apply(func), args.map(Tokenizer.apply))
  }

  def apply(func: String, args: Seq[Token]): Operation = {
    val path = func.split("\\.").toSeq
    val reference = path.tail.foldLeft(Reference(Ast.identifier(path.head)))((acc, pathElement) => {
      Reference(acc, Constant(pathElement))
    })
    Operation(FunctionCall, args, reference)
  }

  def apply(func: Constant, args: Seq[Token]): Operation = {
    Operation(FunctionCall, args, func)
  }

  def apply(func: Token, args: Seq[Token]): Operation = {
    Operation(FunctionCall, args, func)
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(s"${operands.head.value}(${operands.tail.map(_.value).mkString(", ")})")
  }
}
