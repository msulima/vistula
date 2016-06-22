package pl.msulima.vistula.transpiler.rpn

import pl.msulima.vistula.parser.Ast

object Name {

  def apply: PartialFunction[Ast.expr, Token] = {
    case Ast.expr.Name(id, Ast.expr_context.Load) =>
      MutableOperand(id.name)
    case Ast.expr.Attribute(expr, id, Ast.expr_context.Load) =>
      val x = ConstantOperation(RxFlatMap, Seq(Tokenizer.box(expr), ConstantOperand(id.name)))
      println(x)
      x
  }
}
