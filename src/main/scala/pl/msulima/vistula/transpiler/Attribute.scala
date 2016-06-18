package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Attribute {

  def apply: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Attribute(expr, identifier, Ast.expr_context.Load) =>
      Fragment(s"$$arg.${identifier.name}", RxFlatMap, Seq(expr))
  }
}
