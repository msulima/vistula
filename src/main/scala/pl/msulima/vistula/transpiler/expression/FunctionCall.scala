package pl.msulima.vistula.transpiler.expression

import pl.msulima.vistula.parser.Ast
import pl.msulima.vistula.transpiler.{CodeTemplate, RxFlatMap, Scope, Transpiler}

object FunctionCall {

  def apply(scope: Scope): PartialFunction[Ast.expr, CodeTemplate] = {
    case Ast.expr.Call(Ast.expr.Name(Ast.identifier(func), Ast.expr_context.Load), args, _, _, _) =>
      CodeTemplate(s"$func(${arguments(scope, args)})", RxFlatMap, Seq())
    case Ast.expr.Call(func, args, _, _, _) =>
      CodeTemplate(s"%s(${arguments(scope, args)})", RxFlatMap, Seq(func))
  }

  private def arguments(scope: Scope, args: Seq[Ast.expr]) = {
    args.map(arg => Transpiler(scope.copy(mutable = true), arg)).mkString(", ")
  }
}
