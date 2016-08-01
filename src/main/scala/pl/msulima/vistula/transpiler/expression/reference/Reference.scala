package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Tokenizer, _}

case object Reference extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Name(id, Ast.expr_context.Load) =>
      Reference(id)
    case Ast.expr.Attribute(expr, id, Ast.expr_context.Load) =>
      Reference(Tokenizer.apply(expr), Constant(id.name))
  }

  def apply(func: String): Token = {
    val path = func.split("\\.").toSeq
    path.tail.foldLeft(Reference(Ast.identifier(path.head)))((acc, pathElement) => {
      Reference(acc, Constant(pathElement))
    })
  }

  def apply(id: Ast.identifier): Token = {
    Operation(Reference, Seq(), Constant(id.name))
  }

  def apply(source: Token, attribute: Token): Token = {
    Operation(Reference, Seq(source), attribute)
  }

  override def apply(operands: List[Constant], output: Constant): Constant = {
    if (operands.isEmpty) {
      output
    } else {
      Constant(s"${operands.head.value}.${output.value}")
    }
  }
}
