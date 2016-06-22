package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.util.ToArray

object Primitives {

  def apply: PartialFunction[Ast.expr, Token] = {
    case expr: Ast.expr if static.isDefinedAt(expr) =>
      Constant(static(expr))
    case Ast.expr.List(elts, Ast.expr_context.Load) =>
      Tokenizer.box(Operation(StaticArray(elts.size), elts.map(x => Tokenizer.box(x))))
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
