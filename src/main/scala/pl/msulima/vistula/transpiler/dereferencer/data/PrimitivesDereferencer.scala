package pl.msulima.vistula.transpiler.dereferencer.data

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.dereferencer.Dereferencer
import pl.msulima.vistula.transpiler.scope.{ClassReference, ScopeElement}
import pl.msulima.vistula.util.ToArray

object PrimitivesDereferencer {

  val StaticNull = ExpressionConstant("null", ScopeElement.DefaultConst)

}

trait PrimitivesDereferencer {
  this: Dereferencer =>

  private val MagicInlineJavascriptPrefix = "# javascript\n"

  def primitivesDereferencer: PartialFunction[Token, Expression] = {
    case Direct(Ast.stmt.Expr(Ast.expr.Tuple(expr +: _, Ast.expr_context.Load))) =>
      val body = dereference(expr)
      if (body.`type`.observable) {
        body
      } else {
        ExpressionOperation(Tuple, body)
      }
    case Direct(Ast.stmt.Expr(expr)) if static.isDefinedAt(expr) =>
      val (value, t) = static(expr)
      dereference(TypedConstant(value, ScopeElement.const(t)))
    case Direct(Ast.stmt.Expr(Ast.expr.Name(Ast.identifier("None"), Ast.expr_context.Load))) =>
      PrimitivesDereferencer.StaticNull
    case Direct(Ast.stmt.Expr(Ast.expr.Str(x))) if x.startsWith(MagicInlineJavascriptPrefix) =>
      val content = x.stripPrefix(MagicInlineJavascriptPrefix)

      ExpressionConstant(content, ScopeElement.Unit)
    case Direct(Ast.stmt.Expr(Ast.expr.Str(x))) =>
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

  def apply(x: String): Expression = {
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

  def apply(elements: Seq[Expression]): Expression = {
    ExpressionOperation(StaticArray, elements, ScopeElement.DefaultConst)
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

case object Tuple extends Operator {

  override def apply(operands: List[Constant]): String = {
    s"(${operands.head.value})"
  }
}
