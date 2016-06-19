package pl.msulima.vistula.transpiler.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{CodeTemplate, Scope, Static, Transpiler}

object Tuple {

  def apply(scope: Scope): PartialFunction[Ast.expr, CodeTemplate] = {
    case Ast.expr.Tuple(expr +: _, Ast.expr_context.Load) =>
      CodeTemplate(s"(${Transpiler.scoped(scope, expr).result.code})", Static)
  }
}
