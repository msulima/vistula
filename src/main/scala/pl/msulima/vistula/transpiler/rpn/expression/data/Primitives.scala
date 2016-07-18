package pl.msulima.vistula.transpiler.rpn.expression.data

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.rpn.{Tokenizer, _}
import pl.msulima.vistula.util.ToArray

object Primitives {

  def apply: PartialFunction[Ast.expr, Token] = {
    case expr: Ast.expr if static.isDefinedAt(expr) =>
      Constant(static(expr))
    case Ast.expr.Str(x) =>
      StaticString(x)
    case Ast.expr.List(elts, Ast.expr_context.Load) =>
      StaticArray(elts.map(expr => Tokenizer.boxed(expr)))
    case Ast.expr.Dict(keys, values) =>
      val dict = keys.zip(values).flatMap({
        case (Ast.expr.Str(key), expr) =>
          Seq(StaticString(key), Tokenizer.boxed(expr))
      })
      Operation(StaticDict, dict, Tokenizer.Ignored)
  }

  private def static: PartialFunction[Ast.expr, String] = {
    case Ast.expr.Num(x) =>
      x.toString
    case Ast.expr.Name(Ast.identifier("None"), Ast.expr_context.Load) =>
      "null"
    case Ast.expr.Name(Ast.identifier("False"), Ast.expr_context.Load) =>
      "false"
    case Ast.expr.Name(Ast.identifier("True"), Ast.expr_context.Load) =>
      "true"
  }
}

case object StaticString extends Operator {

  def apply(x: String): Token = {
    Operation(StaticString, Seq(), Constant(x))
  }

  def apply(operands: List[Constant], output: Constant): Constant = {
    Constant( s""""${output.value}"""")
  }
}

case object StaticArray extends Operator {

  def apply(elements: Seq[Token]): Token = {
    Operation(StaticArray, elements, Tokenizer.Ignored)
  }

  def apply(operands: List[Constant], output: Constant): Constant = {
    Constant(ToArray(operands.map(_.value)))
  }
}

case object StaticDict extends Operator {

  def apply(operands: List[Constant], output: Constant): Constant = {
    val toSeq: Seq[List[Constant]] = operands.grouped(2).toSeq

    Constant(ToArray.toDict(toSeq.map({
      case key :: value :: Nil =>
        (key.value, value.value)
    })))
  }
}
