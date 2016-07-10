package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template

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
    val byRpn = rpn.Transformer.applyExpr(scope).andThen(c => CodeTemplate(c.program.map(rpn.Transpiler.toConstant).mkString(";"), Static))

    byRpn.orElse(Generator.apply).orElse(template.transpiler.Expression.apply)
  }
}
