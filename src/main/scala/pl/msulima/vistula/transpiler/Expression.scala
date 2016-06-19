package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template
import pl.msulima.vistula.transpiler.expression._

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

      scope(result)
  }

  private def foo(expr: Ast.expr): Result = {
    val codeTemplate = parseExpression(expr)
    val upstreamFragments = codeTemplate.dependencies.map(foo)
    val nextMutable = upstreamFragments.exists(_.mutable) || codeTemplate.mapper != Static

    Result(codeTemplate.resolve(upstreamFragments), mutable = nextMutable)
  }

  private lazy val parseExpression: PartialFunction[Ast.expr, CodeTemplate] = {
    Generator.apply.orElse(Attribute.apply).orElse(template.transpiler.Expression.apply).orElse(Primitives.apply(scope))
      .orElse(new Name(scope).apply).orElse(FunctionCall.apply(scope)).orElse(Arithmetic.apply).orElse(Lambda.apply)
      .orElse(Dereference.apply).orElse(Tuple.apply(scope))
  }
}
