package pl.msulima.vistula.transpiler

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.template
import pl.msulima.vistula.transpiler.expression.{Arithmetic, FunctionCall, Lambda}
import pl.msulima.vistula.util.ToArray

case class Result(code: String, mutable: Boolean)

object Expression {

  def apply: PartialFunction[Ast.stmt, String] = {
    case Ast.stmt.Expr(value) =>
      val result = foo(value)
      if (result.mutable) {
        result.code
      } else {
        s"vistula.constantObservable(${result.code})"
      }
  }

  private def foo(expr: Ast.expr): Result = {
    val fragment = parseExpression(expr)

    val upstreamFragments = fragment.dependencies.map(foo)

    if (!upstreamFragments.exists(_.mutable) && fragment.mapper == Static) {
      Result(fragment.template.format(upstreamFragments.map(_.code): _*), mutable = false)
    } else {
      fragment.mapper match {
        case Static | RxMap =>
          Result(asString(fragment, upstreamFragments, "rxMap"), mutable = true)
        case RxFlatMap =>
          Result(asString(fragment, upstreamFragments, "rxFlatMap"), mutable = true)
      }
    }
  }

  private def asString(fragment: Fragment, upstreamFragments: Seq[Result], mapper: String) = {
    val mutableDependencies = upstreamFragments.filter(_.mutable)

    if (mutableDependencies.isEmpty) {
      fragment.template.format(upstreamFragments.map(_.code): _*)
    } else if (mutableDependencies.size == 1) {
      val operands = upstreamFragments.map({
        case result if result.mutable =>
          "$arg"
        case result =>
          result.code
      })

      val code = fragment.template.format(operands: _*)
      s"""${mutableDependencies.head.code}.$mapper($$arg => ($code))"""
    } else {
      var mutableOperandsIndex = 0
      val operands = upstreamFragments.map({
        case result if result.mutable =>
          val op = s"$$args[$mutableOperandsIndex]"
          mutableOperandsIndex = mutableOperandsIndex + 1
          op
        case result =>
          result.code
      })
      val arguments = ToArray(mutableDependencies.map(_.code))
      val code = fragment.template.format(operands: _*)
      s"""vistula.zip($arguments).$mapper($$args => ($code))"""
    }
  }

  lazy val parseExpression: PartialFunction[Ast.expr, Fragment] = {
    Generator.apply.orElse(Attribute.apply).orElse(template.transpiler.Expression.apply).orElse(Primitives.apply)
      .orElse(loadName).orElse(FunctionCall.apply).orElse(Arithmetic.apply).orElse(Lambda.apply)
  }

  private lazy val loadName: PartialFunction[Ast.expr, Fragment] = {
    case Ast.expr.Name(Ast.identifier(x), Ast.expr_context.Load) =>
      Fragment(x, mapper = RxMap)
  }
}
