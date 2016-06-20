package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast

object Attribute {

  def apply(scope: Scope): PartialFunction[Ast.expr, CodeTemplate] = {
    case Ast.expr.Attribute(expr, identifier, Ast.expr_context.Load) if scope.mutable =>
      CodeTemplate(s"$$arg.${identifier.name}", RxFlatMap, Seq(expr))
    case Ast.expr.Attribute(expr, identifier, Ast.expr_context.Load) =>
      CodeTemplate(s"%s.${identifier.name}", Static, Seq(expr))
    case Ast.expr.Attribute(expr, identifier, Ast.expr_context.Dereference) => // FIXME: switch
      CodeTemplate(s"%s.${identifier.name}", Static, Seq(expr))
  }
}
