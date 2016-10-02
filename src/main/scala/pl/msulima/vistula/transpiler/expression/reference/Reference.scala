package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Tokenizer, _}

case object Reference extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Name(id, Ast.expr_context.Load) =>
      Reference(id)
    case Ast.expr.Attribute(expr, id, Ast.expr_context.Load) =>
      Reference(Tokenizer.apply(expr), id)
  }

  def apply(func: String): Token = {
    val path = func.split("\\.").toSeq
    path.tail.foldLeft(Reference(Ast.identifier(path.head)))((acc, pathElement) => {
      Reference(acc, Ast.identifier(pathElement))
    })
  }

  def apply(id: Ast.identifier): Token = {
    Operation(Reference, Seq(Constant(id.name)))
  }

  def apply(source: Token, attribute: Ast.identifier): Token = {
    Operation(Reference, Seq(source, Constant(attribute.name)))
  }

  override def apply(operands: List[Constant]): String = {
    if (operands.size == 1) {
      operands.head.value
    } else {
      s"${operands.head.value}.${operands(1).value}"
    }
  }
}
