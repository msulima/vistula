package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast

object Name {

  def apply: PartialFunction[Ast.expr, Seq[Token]] = {
    case Ast.expr.Name(id, Ast.expr_context.Load) =>
      Seq(MutableOperand(id.name))
  }
}
