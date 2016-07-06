package pl.msulima.vistula.transpiler.rpn.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Tokenizer, _}
import pl.msulima.vistula.util.ToArray

object Primitives {

  def apply: PartialFunction[Ast.expr, Token] = {
    case expr: Ast.expr if static.isDefinedAt(expr) =>
      Constant(static(expr))
    case Ast.expr.List(elts, Ast.expr_context.Load) =>
      Box(Operation(StaticArray(elts.size), elts.map(expr => Box(Tokenizer.apply(expr)))))
  }

  private def static: PartialFunction[Ast.expr, String] = {
    case Ast.expr.Num(x) =>
      x.toString
    case Ast.expr.Str(x) =>
      s""""$x""""
    case Ast.expr.Name(Ast.identifier("None"), Ast.expr_context.Load) =>
      "null"
    case Ast.expr.Name(Ast.identifier("False"), Ast.expr_context.Load) =>
      "false"
    case Ast.expr.Name(Ast.identifier("True"), Ast.expr_context.Load) =>
      "true"
  }
}

case class StaticArray(operands: Int) extends Operator {

  def apply(operands: List[Constant]): Constant = {
    Constant(ToArray(operands.map(_.value)))
  }
}
