package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template
import pl.msulima.vistula.transpiler.expression.{Arithmetic, FunctionCall, Lambda}

case class Result(code: String, mutable: Boolean)

object Expression {

  def apply(scope: Scope): PartialFunction[Ast.stmt, ScopedResult] = {
    new Expression(scope).apply
  }
}

class Expression(scope: Scope) {

  def apply: PartialFunction[Ast.stmt, ScopedResult] = {
    case Ast.stmt.Expr(value) =>
      val result = foo(value)

      scope {
        if (result.mutable || !scope.mutable) {
          result.code
        } else {
          s"vistula.constantObservable(${result.code})"
        }
      }
  }

  private def foo(expr: Ast.expr): Result = {
    val codeTemplate = parseExpression(expr)
    val upstreamFragments = codeTemplate.dependencies.map(foo)
    val nextMutable = upstreamFragments.exists(_.mutable) || codeTemplate.mapper != Static

    Result(codeTemplate.resolve(upstreamFragments), mutable = nextMutable)
  }

  private lazy val parseExpression: PartialFunction[Ast.expr, CodeTemplate] = {
    Generator.apply.orElse(Attribute.apply).orElse(template.transpiler.Expression.apply).orElse(Primitives.apply)
      .orElse(loadName).orElse(FunctionCall.apply).orElse(Arithmetic.apply).orElse(Lambda.apply)
  }

  private lazy val loadName: PartialFunction[Ast.expr, CodeTemplate] = {
    case Ast.expr.Name(id, Ast.expr_context.Load) =>
      val mapper = if (scope.variables.contains(id)) {
        Static
      } else {
        RxMap
      }
      CodeTemplate(id.name, mapper = mapper)
  }
}
