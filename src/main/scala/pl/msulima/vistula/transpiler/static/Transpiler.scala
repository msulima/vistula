package pl.msulima.vistula.transpiler.static

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler._
import pl.msulima.vistula.transpiler.expression.Arithmetic

object Transpiler {

  def apply(scope: Scope, stmt: Ast.stmt): String = {
    stmt match {
      case Ast.stmt.Expr(expr) =>
        parseExpression(scope, expr)
    }
  }

  def parseExpression(scope: Scope, expr: Ast.expr) = {
    Primitives.static.orElse(arithmetic)(expr)
  }

  private val arithmetic = Arithmetic.apply.andThen(fragment => {
    fragment.template
  })
}
