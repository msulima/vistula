package pl.msulima.vistula.transpiler.expression.reference

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{Tokenizer, _}

case object Reference extends Operator {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Name(id, Ast.expr_context.Load) if !Seq("None", "False", "True").contains(id.name) =>
      Reference(id)
    case Ast.expr.Attribute(expr, id, Ast.expr_context.Load) =>
      Reference(Tokenizer.apply(expr), id)
  }

  def apply(id: Ast.identifier): Token = {
    Operation(Reference, Seq(IdConstant(id)))
  }

  def apply(source: Token, attribute: Ast.identifier): Token = {
    Operation(Reference, Seq(source, IdConstant(attribute)))
  }

  override def apply(operands: List[Constant]): String = {
    if (operands.size == 1) {
      operands.head.value
    } else {
      s"${operands.head.value}.${operands(1).value}"
    }
  }
}
