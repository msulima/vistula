package pl.msulima.vistula.transpiler.expression.data

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopeElement}
import pl.msulima.vistula.util.ToArray

object Primitives {

  val StaticNull = TypedConstant("null", ScopeElement.DefaultConst)

  def apply: PartialFunction[Ast.expr, Token] = {
    case expr: Ast.expr if static.isDefinedAt(expr) =>
      val (value, t) = static(expr)
      TypedConstant(value, ScopeElement.const(t))
    case Ast.expr.Name(Ast.identifier("None"), Ast.expr_context.Load) =>
      StaticNull
    case Ast.expr.Str(x) =>
      StaticString(x)
  }

  private def static: PartialFunction[Ast.expr, (String, ClassReference)] = {
    case Ast.expr.Num(x) =>
      x.toString -> ClassReference.Integer
    case Ast.expr.Name(Ast.identifier("False"), Ast.expr_context.Load) =>
      "false" -> ClassReference.Boolean
    case Ast.expr.Name(Ast.identifier("True"), Ast.expr_context.Load) =>
      "true" -> ClassReference.Boolean
  }
}

case object StaticString extends Operator {

  def apply(x: String): Token = {
    Operation(StaticString, Seq(TypedConstant(x, ScopeElement.const(ClassReference.String))))
  }

  def toExpression(x: String): Expression = {
    ExpressionOperation(StaticString, ExpressionConstant(x, ScopeElement.const(ClassReference.String)))
  }

  override def apply(operands: List[Constant]) = {
    escape(operands.head.value)
  }

  private def escape(text: String) = {
    s""""${text.replaceAll("\n", """\\\n""")}""""
  }
}

case object StaticArray extends Operator {

  def apply(elements: Seq[Token]): Token = {
    Operation(StaticArray, elements)
  }

  def expr(operands: Seq[Expression]): Expression = {
    ExpressionOperation(StaticArray, operands, ScopeElement.DefaultConst)
  }

  override def apply(operands: List[Constant]): String = {
    ToArray(operands.map(_.value))
  }
}

case object StaticDict extends Operator {

  override def apply(operands: List[Constant]): String = {
    val toSeq: Seq[List[Constant]] = operands.grouped(2).toSeq

    ToArray.toDict(toSeq.map({
      case key :: value :: Nil =>
        (key.value, value.value)
    }))
  }
}
